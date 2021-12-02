package xfacthd.recipebuilder.common.net;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import xfacthd.recipebuilder.common.container.TagBuilderContainer;

import java.util.function.Supplier;

public class PacketOpenTagBuilder
{
    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> openContainer(ctx.get().getSender()));
        return true;
    }

    private void openContainer(ServerPlayer player)
    {
        NetworkHooks.openGui(player, new MenuProvider()
        {
            @Override
            public Component getDisplayName() { return TagBuilderContainer.TITLE; }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player playerEntity)
            {
                return new TagBuilderContainer(containerId, playerInv);
            }
        });
    }
}