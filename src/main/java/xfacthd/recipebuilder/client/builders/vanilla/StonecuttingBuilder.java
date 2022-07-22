package xfacthd.recipebuilder.client.builders.vanilla;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import xfacthd.recipebuilder.client.data.*;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;

import java.util.HashMap;
import java.util.Map;

public class StonecuttingBuilder extends AbstractBuilder
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/stonecutter.png");

    public StonecuttingBuilder()
    {
        super(RecipeSerializer.STONECUTTER, "minecraft", new ItemStack(Blocks.STONECUTTER), buildSlotMap(), TEXTURE, 19, 14, 145, 56, true);
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents) { }

    @Override
    protected void build(Map<String, Pair<RecipeSlot<?, ?>, SlotContent<?>>> contents, String recipeName, CriterionTriggerInstance criterion, String criterionName)
    {
        Ingredient input = getContentAsIngredient(contents.get("in").getSecond(), true);
        ItemStack output = getItemContent(contents.get("out").getSecond());

        SingleItemRecipeBuilder builder = SingleItemRecipeBuilder.stonecutting(input, output.getItem(), output.getCount())
                .unlockedBy(criterionName, criterion);

        //noinspection ConstantConditions
        Exporter.exportRecipe(
                cons -> builder.save(cons, output.getItem().getRegistryName()),
                builder::save,
                recipeName
        );
    }



    private static Map<String, RecipeSlot<?, ?>> buildSlotMap()
    {
        Map<String, RecipeSlot<?, ?>> slots = new HashMap<>();

        slots.put("in", new ItemSlot("in", 1, 19, false, true, ItemSlot.SINGLE_ITEM));
        slots.put("out", new ItemSlot("out", 124, 19, false, false));

        return slots;
    }
}