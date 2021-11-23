package xfacthd.recipebuilder.client.screen.edit;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.recipebuilder.client.data.slots.ItemSlot;
import xfacthd.recipebuilder.client.screen.EditSlotScreen;
import xfacthd.recipebuilder.client.screen.widget.SelectionWidget;
import xfacthd.recipebuilder.client.screen.widget.LocationEntry;
import xfacthd.recipebuilder.common.util.Utils;

public class EditItemSlotScreen extends EditSlotScreen<ItemStack, ItemSlot.ItemContent, ItemSlot>
{
    public static final ITextComponent TITLE = Utils.translate(null, "edit_item.title");

    public EditItemSlotScreen(ItemSlot slot, ItemSlot.ItemContent content) { super(TITLE, slot, content); }

    @Override
    protected void renderContent(MatrixStack mstack, int slotX, int slotY, int mouseX, int mouseY)
    {
        slot.renderContent(this, content, mstack, slotX, slotY, 2000, font);
    }

    @Override
    protected void gatherTags(ItemStack content, SelectionWidget<LocationEntry> widget)
    {
        content.getItem().getTags().forEach(tag -> widget.addEntry(new LocationEntry(tag)));
    }
}