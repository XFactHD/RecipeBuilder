package xfacthd.recipebuilder.client.builders.vanilla;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Blocks;
import net.minecraft.data.SmithingRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;

import java.util.HashMap;
import java.util.Map;

public class SmithingBuilder extends AbstractBuilder
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/smithing.png");

    public SmithingBuilder()
    {
        super(IRecipeSerializer.SMITHING, "minecraft", new ItemStack(Blocks.SMITHING_TABLE), buildSlotMap(), TEXTURE, 17, 7, 134, 57, true);
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents) { }

    @Override
    protected void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, ICriterionInstance criterion, String criterionName)
    {
        Ingredient base = getContentAsIngredient(contents.get("base").getSecond(), true);
        Ingredient addition = getContentAsIngredient(contents.get("base").getSecond(), true);
        Item output = getItemContent(contents.get("out").getSecond()).getItem();

        SmithingRecipeBuilder builder = SmithingRecipeBuilder.smithing(base, addition, output)
                .unlocks(criterionName, criterion);

        //noinspection ConstantConditions
        Exporter.exportRecipe(
                cons -> builder.save(cons, output.getRegistryName()),
                builder::save,
                recipeName
        );
    }



    private static Map<String, RecipeSlot<?>> buildSlotMap()
    {
        Map<String, RecipeSlot<?>> slots = new HashMap<>();

        slots.put("base", new ItemSlot("base", 10, 40, false, true, ItemSlot.SINGLE_ITEM));
        slots.put("addition", new ItemSlot("addition", 59, 40, false, true, ItemSlot.SINGLE_ITEM));
        slots.put("out", new ItemSlot("out", 117, 40, false, false, ItemSlot.SINGLE_ITEM));

        return slots;
    }
}