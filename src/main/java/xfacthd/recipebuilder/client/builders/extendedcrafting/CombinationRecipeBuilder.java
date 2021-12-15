package xfacthd.recipebuilder.client.builders.extendedcrafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.compat.ExtendedCraftingCompat;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.client.data.slots.IntegerSlot;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;
import xfacthd.recipebuilder.client.util.BuilderException;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CombinationRecipeBuilder extends AbstractBuilder
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(RecipeBuilder.MOD_ID, "textures/extendedcrafting_crafting_core.png");
    public static final ITextComponent TITLE_POWER_COST = Utils.translate(null, "crafting_core.slot.power_cost");
    public static final ITextComponent TITLE_POWER_RATE = Utils.translate(null, "crafting_core.slot.power_rate");
    public static final ITextComponent MSG_INVALID_POWER_COST =  Utils.translate("msg", "crafting_core.slot.invalid_power_cost");
    public static final ITextComponent MSG_INVALID_POWER_RATE =  Utils.translate("msg", "crafting_core.slot.invalid_power_rate");

    public CombinationRecipeBuilder()
    {
        //noinspection ConstantConditions
        super(
                ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "combination")),
                ExtendedCraftingCompat.MOD_ID,
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "crafting_core")).getDefaultInstance(),
                buildSlotMap(),
                TEXTURE,
                0, 0,
                230, 126,
                false
        );
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents)
    {
        checkAnyFilledExcept(contents, "out", "power", "rate");

        int powerRate = getIntegerContent(contents.get("rate").getSecond());
        if (powerRate <= 0)
        {
            throw new BuilderException(MSG_INVALID_POWER_RATE);
        }

        if (getIntegerContent(contents.get("power").getSecond()) < powerRate)
        {
            throw new BuilderException(MSG_INVALID_POWER_COST);
        }
    }

    @Override
    protected void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, ICriterionInstance criterion, String criterionName)
    {
        List<Ingredient> ingredients = contents.values()
                .stream()
                .filter(pair -> pair.getFirst().getName().length() == 2) //Filter for numbered slots
                .map(Pair::getSecond)
                .filter(content -> !content.isEmpty())
                .map(content -> getContentAsIngredient(content, true))
                .collect(Collectors.toList());

        Ingredient input = getContentAsIngredient(contents.get("input").getSecond(), true);
        ItemStack output = getItemContent(contents.get("out").getSecond());

        BiConsumer<Consumer<IFinishedRecipe>, String> save = (cons, name) ->
                cons.accept(new CombinationResult(
                        new ResourceLocation(name),
                        input,
                        ingredients,
                        output,
                        getIntegerContent(contents.get("power").getSecond()),
                        getIntegerContent(contents.get("rate").getSecond())
                ));

        //noinspection ConstantConditions
        Exporter.exportRecipe(cons -> save.accept(cons, output.getItem().getRegistryName().toString()), save, recipeName);
    }



    private static Map<String, RecipeSlot<?>> buildSlotMap()
    {
        Map<String, RecipeSlot<?>> slots = new HashMap<>();

        slots.put("input", new ItemSlot("input", 1, 55, false, true, ItemSlot.SINGLE_ITEM));

        for (int x = 0; x < 7; x++)
        {
            for (int y = 0; y < 7; y++)
            {
                if (x == 3 && y == 3) { continue; }

                String name = x + "" + y;
                slots.put(name, new ItemSlot(name, 49 + (18 * x), 1 + (18 * y), true, true, ItemSlot.SINGLE_ITEM));
            }
        }

        slots.put("out", new ItemSlot("out", 209, 55, false, false));

        slots.put("power", new IntegerSlot("power", false, TITLE_POWER_COST));
        slots.put("rate", new IntegerSlot("rate", false, TITLE_POWER_RATE));

        return slots;
    }

    private class CombinationResult implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Ingredient input;
        private final List<Ingredient> ingredients;
        private final ItemStack output;
        private final int powerCost;
        private final int powerRate;

        private CombinationResult(ResourceLocation id, Ingredient input, List<Ingredient> ingredients, ItemStack output, int powerCost, int powerRate)
        {
            this.id = id;
            this.input = input;
            this.ingredients = ingredients;
            this.output = output;
            this.powerCost = powerCost;
            this.powerRate = powerRate;
        }

        @Override
        public void serializeRecipeData(JsonObject json)
        {
            json.add("input", input.toJson());

            JsonArray ingredientArray = new JsonArray();
            ingredients.forEach(ing -> ingredientArray.add(ing.toJson()));
            json.add("ingredients", ingredientArray);

            JsonObject result = new JsonObject();
            //noinspection ConstantConditions
            result.addProperty("item", ForgeRegistries.ITEMS.getKey(output.getItem()).toString());
            if (output.getCount() > 1)
            {
                result.addProperty("count", output.getCount());
            }
            json.add("result", result);

            json.addProperty("powerCost", powerCost);
            json.addProperty("powerRate", powerRate);
        }

        @Override
        public ResourceLocation getId() { return id; }

        @Override
        public IRecipeSerializer<?> getType() { return CombinationRecipeBuilder.this.getType(); }

        @Override
        public JsonObject serializeAdvancement() { return null; }

        @Override
        public ResourceLocation getAdvancementId() { return null; }
    }
}
