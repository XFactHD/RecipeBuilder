package xfacthd.recipebuilder.client.screen.edit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;
import xfacthd.recipebuilder.client.screen.EditSlotScreen;
import xfacthd.recipebuilder.client.screen.widget.SelectionWidget;
import xfacthd.recipebuilder.client.screen.widget.LocationEntry;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.stream.Stream;

public class EditItemSlotScreen extends EditSlotScreen<Item, ItemStack, ItemSlot.ItemContent, ItemSlot>
{
    public static final Component TITLE = Utils.translate(null, "edit_item.title");

    public EditItemSlotScreen(ItemSlot slot, ItemSlot.ItemContent content) { super(TITLE, slot, content); }

    @Override
    protected void renderContent(PoseStack pstack, int slotX, int slotY, int mouseX, int mouseY)
    {
        slot.renderContent(this, content, pstack, slotX, slotY, 2000, font);
    }

    @Override
    protected void gatherTags(ItemStack content, SelectionWidget<LocationEntry> widget)
    {
        //noinspection ConstantConditions
        ForgeRegistries.ITEMS.tags()
                .getReverseTag(content.getItem())
                .map(IReverseTag::getTagKeys)
                .orElseGet(Stream::of)
                .forEach(tag -> widget.addEntry(new LocationEntry(tag.location())));
    }
}