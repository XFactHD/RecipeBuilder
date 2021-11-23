package xfacthd.recipebuilder.client.builders.vanilla;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import xfacthd.recipebuilder.client.util.BuilderException;
import xfacthd.recipebuilder.client.data.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShapelessCraftingBuilder extends AbstractBuilder
{
    public ShapelessCraftingBuilder()
    {
        super(IRecipeSerializer.SHAPELESS_RECIPE, "minecraft", ShapedCraftingBuilder.buildSlotMap(), ShapedCraftingBuilder.TEXTURE, 29, 16, 116, 54, true);
    }

    @Override
    protected void validate(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents)
    {
        AtomicBoolean foundInput = new AtomicBoolean();

        contents.forEach((name, pair) ->
        {
            if (!name.equals("out"))
            {
                if (!pair.getSecond().isEmpty())
                {
                    foundInput.set(true);
                }
            }
        });

        if (!foundInput.get())
        {
            throw new BuilderException(ShapedCraftingBuilder.MSG_INPUT_EMPTY);
        }
    }

    @Override
    protected void build(Map<String, Pair<RecipeSlot<?>, SlotContent<?>>> contents, String recipeName, ICriterionInstance criterion, String criterionName)
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