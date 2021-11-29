package xfacthd.recipebuilder.common.net;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.recipebuilder.common.container.RecipeBuilderContainer;

import java.util.function.Supplier;

public class PacketOpenRecipeBuilder
{
    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> openContainer(ctx.get().getSender()));
        return true;
    }

    private void openContainer(ServerPlayerEntity player)
    {
        NetworkHooks.openGui(player, new INamedContainerProvider()
        {
            @Override
            public ITextComponent getDisplayName() { return RecipeBuilderContainer.TITLE; }

            @Override
            public Container createMenu(int containerId, PlayerInventory playerInv, PlayerEntity playerEntity)
            {
                return new RecipeBuilderContainer(containerId, playerInv);
            }
        });
    }
}