package xfacthd.recipebuilder.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.recipebuilder.RecipeBuilder;

public class TagBuilderContainer extends Container
{
    public static final ITextComponent TITLE = new TranslationTextComponent("recipebuilder.tag_builder.title");

    public TagBuilderContainer(int containerId, PlayerInventory playerInv)
    {
        super(RecipeBuilder.TAG_BUILDER_CONTAINER.get(), containerId);

        for(int row = 0; row < 3; ++row)
        {
            for(int column = 0; column < 9; ++column)
            {
                addSlot(new Slot(playerInv, column + row * 9 + 9, 132 + column * 18, 168 + row * 18));
            }
        }

        for(int column = 0; column < 9; ++column)
        {
            addSlot(new Slot(playerInv, column, 132 + column * 18, 226));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) { return true; }

    @Override
    public ItemStack quickMoveStack(PlayerEntity pPlayer, int pIndex) { return ItemStack.EMPTY; }
}