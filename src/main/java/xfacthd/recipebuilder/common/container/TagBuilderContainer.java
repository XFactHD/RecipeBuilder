package xfacthd.recipebuilder.common.container;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import xfacthd.recipebuilder.RecipeBuilder;

public class TagBuilderContainer extends AbstractContainerMenu
{
    public static final Component TITLE = new TranslatableComponent("recipebuilder.tag_builder.title");

    public TagBuilderContainer(int containerId, Inventory playerInv)
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
    public boolean stillValid(Player player) { return true; }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) { return ItemStack.EMPTY; }
}