package xfacthd.recipebuilder.client.builders.vanilla;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;

import java.util.*;

public class ShapedCraftingBuilder extends AbstractBuilder
{
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    public ShapedCraftingBuilder()
    {
        super(IRecipeSerializer.SHAPED_RECIPE, "minecraft", new ItemStack(Blocks.CRAFTING_TABLE), buildSlotMap(), TEXTURE, 29, 16, 116, 54, true);
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents)
    {
        checkAnyFilledExcept(contents, "out");
    }

    @Override
    protected void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, ICriterionInstance criterion, String criterionName)
    {
        ItemStack out = getItemContent(contents.get("out").getSecond());

        ShapedRecipeBuilder builder = new ShapedRecipeBuilder(out.getItem(), out.getCount());

        Map<Item, Character> itemKeys = new HashMap<>();
        Map<ITag<Item>, Character> tagKeys = new HashMap<>();
        List<String> lines = parseTableGridLines(3, contents, itemKeys, tagKeys);

        itemKeys.forEach((i, c) -> builder.define(c, i));
        tagKeys.forEach((t, c) -> builder.define(c, t));
        lines.forEach(builder::pattern);

        builder.unlockedBy(criterionName, criterion);

        Exporter.exportRecipe(builder::save, builder::save, recipeName);
    }



    public static Map<String, RecipeSlot<?>> buildSlotMap()
    {
        Map<String, RecipeSlot<?>> slots = new HashMap<>();

        slots.put("00", new ItemSlot("00",  1,  1, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("01", new ItemSlot("01", 19,  1, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("02", new ItemSlot("02", 37,  1, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("10", new ItemSlot("10",  1, 19, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("11", new ItemSlot("11", 19, 19, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("12", new ItemSlot("12", 37, 19, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("20", new ItemSlot("20",  1, 37, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("21", new ItemSlot("21", 19, 37, true, true, ItemSlot.SINGLE_ITEM));
        slots.put("22", new ItemSlot("22", 37, 37, true, true, ItemSlot.SINGLE_ITEM));

        slots.put("out", new ItemSlot("out", 95, 19, false, false));

        return slots;
    }
}