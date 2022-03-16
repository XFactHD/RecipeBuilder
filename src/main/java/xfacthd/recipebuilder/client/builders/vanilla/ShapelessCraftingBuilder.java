package xfacthd.recipebuilder.client.builders.vanilla;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import xfacthd.recipebuilder.client.data.*;

import java.util.Map;

public class ShapelessCraftingBuilder extends AbstractBuilder
{
    public ShapelessCraftingBuilder()
    {
        super(RecipeSerializer.SHAPELESS_RECIPE, "minecraft", new ItemStack(Blocks.CRAFTING_TABLE), ShapedCraftingBuilder.buildSlotMap(), ShapedCraftingBuilder.TEXTURE, 29, 16, 116, 54, true);
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

        ShapelessRecipeBuilder builder = new ShapelessRecipeBuilder(out.getItem(), out.getCount());

        for (Pair<RecipeSlot<?>, SlotContent<?>> pair : contents.values())
        {
            if (pair.getFirst().getName().equals("out"))
            {
                continue;
            }

            ItemStack stack = getItemContent(pair.getSecond());
            if (!stack.isEmpty())
            {
                if (pair.getSecond().shouldUseTag())
                {
                    builder.requires(getTagContent(pair.getSecond()));
                }
                else
                {
                    builder.requires(stack.getItem());
                }
            }
        }

        builder.unlockedBy(criterionName, criterion);

        Exporter.exportRecipe(builder::save, builder::save, recipeName);
    }
}