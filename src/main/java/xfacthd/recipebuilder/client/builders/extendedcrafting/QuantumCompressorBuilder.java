package xfacthd.recipebuilder.client.builders.extendedcrafting;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.client.compat.ExtendedCraftingCompat;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.client.data.slots.IntegerSlot;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;
import xfacthd.recipebuilder.client.util.BuilderException;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class QuantumCompressorBuilder extends AbstractBuilder
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "textures/gui/jei/compressor.png");
    public static final Component TITLE_INPUT_COUNT = Utils.translate(null, "compressor.slot.input_count");
    public static final Component TITLE_POWER_COST = Utils.translate(null, "compressor.slot.power_cost");
    public static final Component TITLE_POWER_RATE = Utils.translate(null, "compressor.slot.power_rate");
    public static final Component MSG_INVALID_INPUT_COUNT = Utils.translate("msg", "compressor.slot.invalid_input_count");
    public static final Component MSG_INVALID_POWER_COST =  Utils.translate("msg", "compressor.slot.invalid_power_cost");
    public static final Component MSG_INVALID_POWER_RATE =  Utils.translate("msg", "compressor.slot.invalid_power_rate");

    public QuantumCompressorBuilder()
    {
        //noinspection ConstantConditions
        super(
                ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "compressor")),
                ExtendedCraftingCompat.MOD_ID,
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "compressor")).getDefaultInstance(),
                buildSlotMap(),
                TEXTURE,
                0, 0,
                149, 78,
                false
        );
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents)
    {
        if (getIntegerContent(contents.get("count").getSecond()) <= 0)
        {
            throw new BuilderException(MSG_INVALID_INPUT_COUNT);
        }

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
    protected void build(Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName)
    {
        ItemStack output = getItemContent(contents.get("out").getSecond());

        BiConsumer<Consumer<FinishedRecipe>, String> save = (cons, name) ->
                cons.accept(new QuantumCompressorResult(
                        new ResourceLocation(name),
                        getContentAsIngredient(contents.get("in").getSecond(), true),
                        getContentAsIngredient(contents.get("catalyst").getSecond(), true),
                        output,
                        getIntegerContent(contents.get("count").getSecond()),
                        getIntegerContent(contents.get("power").getSecond()),
                        getIntegerContent(contents.get("rate").getSecond())
                ));

        //noinspection ConstantConditions
        Exporter.exportRecipe(cons -> save.accept(cons, output.getItem().getRegistryName().toString()), save, recipeName);
    }



    private static Map<String, RecipeSlot<?, ?>> buildSlotMap()
    {
        Map<String, RecipeSlot<?, ?>> slots = new HashMap<>();

        slots.put("catalyst", new ItemSlot("catalyst", 31, 31, false, true, ItemSlot.SINGLE_ITEM));
        slots.put("in", new ItemSlot("in", 58, 31, false, true, ItemSlot.SINGLE_ITEM));
        slots.put("out", new ItemSlot("out", 128, 31, false, false));

        slots.put("count", new IntegerSlot("count", false, TITLE_INPUT_COUNT));
        slots.put("power", new IntegerSlot("power", false, TITLE_POWER_COST));
        slots.put("rate", new IntegerSlot("rate", false, TITLE_POWER_RATE));

        return slots;
    }

    private class QuantumCompressorResult implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final Ingredient input;
        private final Ingredient catalyst;
        private final ItemStack output;
        private final int inputCount;
        private final int powerCost;
        private final int powerRate;

        private QuantumCompressorResult(ResourceLocation id, Ingredient input, Ingredient catalyst, ItemStack output, int inputCount, int powerCost, int powerRate)
        {
            this.id = id;
            this.input = input;
            this.catalyst = catalyst;
            this.output = output;
            this.inputCount = inputCount;
            this.powerCost = powerCost;
            this.powerRate = powerRate;
        }

        @Override
        public void serializeRecipeData(JsonObject json)
        {
            json.add("ingredient", input.toJson());
            json.add("catalyst", catalyst.toJson());

            JsonObject result = new JsonObject();
            //noinspection ConstantConditions
            result.addProperty("item", ForgeRegistries.ITEMS.getKey(output.getItem()).toString());
            if (output.getCount() > 1)
            {
                result.addProperty("count", output.getCount());
            }
            json.add("result", result);

            json.addProperty("inputCount", inputCount);
            json.addProperty("powerCost", powerCost);
            json.addProperty("powerRate", powerRate);
        }

        @Override
        public ResourceLocation getId() { return id; }

        @Override
        public RecipeSerializer<?> getType() { return QuantumCompressorBuilder.this.getType(); }

        @Override
        public JsonObject serializeAdvancement() { return null; }

        @Override
        public ResourceLocation getAdvancementId() { return null; }
    }
}
