package xfacthd.recipebuilder.client.builders.extendedcrafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.client.compat.ExtendedCraftingCompat;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;

import java.util.*;

public class ShapedTableBuilder extends AbstractBuilder
{
    private final int tier;

    public ShapedTableBuilder(RecipeSerializer<?> type, String itemName, String textureName, int tier, int texX, int texY, int texWidth, int texHeight)
    {
        //noinspection ConstantConditions
        super(
                type,
                tier > 0 ? "tier_" + tier : null,
                ExtendedCraftingCompat.MOD_ID,
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExtendedCraftingCompat.MOD_ID, itemName)).getDefaultInstance(),
                buildSlotMap(tier),
                new ResourceLocation(ExtendedCraftingCompat.MOD_ID, "textures/gui/" + textureName + ".png"),
                texX, texY,
                texWidth, texHeight,
                false
        );

        this.tier = tier;
    }

    @Override
    public void drawBackground(Screen screen, PoseStack pstack, int builderX, int builderY)
    {
        if (tier == 4)
        {
            RenderSystem.setShaderTexture(0, getTexture());
            GuiComponent.blit(pstack, builderX, builderY, screen.getBlitOffset(), getTexX(), getTexY(), getTexWidth(), getTexHeight(), 512, 512);
        }
        else
        {
            super.drawBackground(screen, pstack, builderX, builderY);
        }
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents)
    {
        checkAnyFilledExcept(contents, "out");
    }

    @Override
    protected void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName)
    {
        ItemStack out = getItemContent(contents.get("out").getSecond());

        Map<Item, Character> itemKeys = new HashMap<>();
        Map<Tag<Item>, Character> tagKeys = new HashMap<>();
        List<String> lines = parseTableGridLines(Math.max(tier, 1) * 2 + 1, contents, itemKeys, tagKeys);

        Map<Character, Ingredient> keys = new HashMap<>();
        itemKeys.forEach((item, c) -> keys.put(c, Ingredient.of(item)));
        tagKeys.forEach((tag, c) -> keys.put(c, Ingredient.of(tag)));

        Exporter.exportRecipe(
                cons -> cons.accept(new ShapedTableResult(out.getItem().getRegistryName(), tier, out, lines, keys)),
                (cons, name) -> cons.accept(new ShapedTableResult(new ResourceLocation(name), tier, out, lines, keys)),
                recipeName
        );
    }



    public static Map<String, RecipeSlot<?>> buildSlotMap(int tier)
    {
        Map<String, RecipeSlot<?>> slots = new HashMap<>();

        int size = Math.max(tier, 1) * 2 + 1;
        for (int x = 0; x < size; x++)
        {
            for (int y = 0; y < size; y++)
            {
                String name = x + "" + y;
                slots.put(name, new ItemSlot(name, 1 + (18 * x), 1 + (18 * y), true, true, ItemSlot.SINGLE_ITEM));
            }
        }

        int x = size * 18 + (tier == 4 ? 37 : (tier == 0 ? 41 : 38));
        int y = (size - 1) / 2 * 18;
        if (tier == 0) { y++; }
        slots.put("out", new ItemSlot("out", x, y, false, false));

        return slots;
    }

    private class ShapedTableResult implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final int tier;
        private final Item result;
        private final int count;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;

        private ShapedTableResult(ResourceLocation id, int tier, ItemStack result, List<String> pattern, Map<Character, Ingredient> key)
        {
            this.id = id;
            this.tier = tier;
            this.result = result.getItem();
            this.count = result.getCount();
            this.pattern = pattern;
            this.key = key;
        }

        public void serializeRecipeData(JsonObject json)
        {
            JsonArray patternArray = new JsonArray();
            pattern.forEach(patternArray::add);
            json.add("pattern", patternArray);

            JsonObject keyList = new JsonObject();
            key.forEach((key1, value) ->
                    keyList.add(String.valueOf(key1), value.toJson())
            );
            json.add("key", keyList);

            JsonObject resultObj = new JsonObject();
            //noinspection ConstantConditions
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
            if (count > 1)
            {
                resultObj.addProperty("count", count);
            }
            json.add("result", resultObj);

            if (tier > 0)
            {
                json.addProperty("tier", tier);
            }
        }

        public RecipeSerializer<?> getType() { return ShapedTableBuilder.this.getType(); }

        public ResourceLocation getId() { return id; }

        public JsonObject serializeAdvancement() { return null; }

        public ResourceLocation getAdvancementId() { return null; }
    }
}
