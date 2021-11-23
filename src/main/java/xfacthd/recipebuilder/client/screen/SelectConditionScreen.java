package xfacthd.recipebuilder.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import xfacthd.recipebuilder.client.screen.widget.HintedTextFieldWidget;
import xfacthd.recipebuilder.client.screen.widget.SelectionWidget;
import xfacthd.recipebuilder.client.util.ClientUtils;
import xfacthd.recipebuilder.client.data.Condition;
import xfacthd.recipebuilder.common.util.Utils;

public class SelectConditionScreen extends ContainerScreen<SelectConditionScreen.DummyContainer>
{
    public static final ITextComponent TITLE = Utils.translate(null, "select_condition.title");
    public static final ITextComponent TITLE_SELECT = Utils.translate(null, "select_condition.select.title");
    public static final ITextComponent TITLE_STACK = Utils.translate(null, "select_condition.item.title");
    public static final ITextComponent TITLE_TAG = Utils.translate(null, "select_condition.tag.title");
    public static final ITextComponent MSG_NO_CONDITION = Utils.translate("msg", "select_condition.no_condition");
    public static final ITextComponent MSG_MISSING_DATA = Utils.translate("msg", "select_condition.missing_data");
    private static final int WIDTH = 176;
    private static final int MAIN_HEIGHT = 112;
    private static final int INV_HEIGHT = 82;
    private static final int HEIGHT = MAIN_HEIGHT + INV_HEIGHT;
    private static final int LEFT_OFFSET = 8;
    private static final int SLOT_X = 70;
    private static final int SLOT_Y = 50;

    private final BuilderScreen parent;
    private SelectionWidget<ConditionEntry> selection = null;
    private ConditionEntry currEntry = null;
    private ItemStack conditionStack = ItemStack.EMPTY;
    private TextFieldWidget conditionTagField = null;

    public SelectConditionScreen(BuilderScreen parent)
    {
        //noinspection ConstantConditions
        super(new DummyContainer(), Minecraft.getInstance().player.inventory, TITLE);
        this.parent = parent;
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    @Override
    protected void init()
    {
        super.init();

        inventoryLabelY = HEIGHT - 4 - INV_HEIGHT - font.lineHeight + 2;

        selection = addWidget(new SelectionWidget<>(leftPos + LEFT_OFFSET, topPos + 20, WIDTH - 16, TITLE_SELECT, this::onEntrySelected));
        selection.addEntry(new ConditionEntry(Condition.HAS_ITEM));
        selection.addEntry(new ConditionEntry(Condition.HAS_ITEM_TAG));
        selection.addEntry(new ConditionEntry(Condition.ENTERED_BLOCK));
        if (currEntry != null)
        {
            selection.setSelected(currEntry, false);
        }

        conditionTagField = addButton(new HintedTextFieldWidget(font, leftPos + LEFT_OFFSET + 1, topPos + 50, WIDTH - 18, 18, conditionTagField, TITLE_TAG));
        conditionTagField.visible = false;

        addButton(new Button(leftPos + (WIDTH / 2) - 30, topPos + MAIN_HEIGHT - 35, 60, 20, MessageScreen.TITLE_BTN_OK, btn -> onConfirm()));
    }

    @Override
    public void render(MatrixStack mstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(mstack);
        super.render(mstack, mouseX, mouseY, partialTicks);
        selection.render(mstack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(MatrixStack mstack, float partialTicks, int mouseX, int mouseY)
    {
        ClientUtils.drawScreenBackground(this, mstack, leftPos, topPos, WIDTH, MAIN_HEIGHT);
        ClientUtils.drawInventoryBackground(this, mstack, leftPos, topPos + MAIN_HEIGHT - 4, true);

        if (currEntry != null && currEntry.getCondition().needsItem())
        {
            font.draw(mstack, TITLE_STACK, leftPos + LEFT_OFFSET, topPos + 55, 0x404040);
            renderConditionSlot(mstack, mouseX, mouseY, leftPos + SLOT_X, topPos + SLOT_Y);
        }
    }

    private void renderConditionSlot(MatrixStack mstack, int mouseX, int mouseY, int slotX, int slotY)
    {
        ClientUtils.drawSlotBackground(this, mstack, slotX, slotY);
        renderSlot(mstack, mouseX, mouseY, slotX + 1, slotY + 1, conditionStack);
    }

    private void renderSlot(MatrixStack mstack, int mouseX, int mouseY, int slotX, int slotY, ItemStack content)
    {
        //don't render when the slot position is below the opened selection menu
        if (selection.isMouseOver(slotX, slotY)) { return; }

        //Item
        if (!content.isEmpty())
        {
            setBlitOffset(2100);
            itemRenderer.blitOffset = 2100.0F;

            RenderSystem.enableDepthTest();
            itemRenderer.renderAndDecorateItem(player(), content, slotX, slotY);
            itemRenderer.renderGuiItemDecorations(font, content, slotX, slotY, null);

            itemRenderer.blitOffset = 0.0F;
            setBlitOffset(0);
        }

        //Hover overlay
        if (mouseX >= slotX && mouseX <= slotX + 16 && mouseY >= slotY && mouseY <= slotY + 16)
        {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);

            fillGradient(mstack, slotX, slotY, slotX + 16, slotY + 16, 0x80ffffff, 0x80ffffff);

            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == 0 && currEntry != null && currEntry.getCondition().needsItem() && !selection.isMouseOver(mouseX, mouseY))
        {
            int slotX = leftPos + SLOT_X + 1;
            int slotY = topPos + SLOT_Y + 1;
            if (mouseX >= slotX && mouseX <= slotX + 16 && mouseY >= slotY && mouseY <= slotY + 16)
            {
                ItemStack carried = player().inventory.getCarried();
                if (!conditionStack.isEmpty() && carried.isEmpty())
                {
                    conditionStack = ItemStack.EMPTY;
                }
                else if (carried != ItemStack.EMPTY)
                {
                    conditionStack = carried.copy();
                    conditionStack.setCount(1);
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void setExistingSelection(Condition condition, ItemStack stack, String tag)
    {
        ConditionEntry entry = selection.stream().filter(e -> e.getCondition() == condition).findFirst().orElse(null);
        if (entry != null)
        {
            selection.setSelected(entry, true);
            conditionStack = stack;
            conditionTagField.setValue(tag);
        }
    }

    private void onEntrySelected(ConditionEntry entry)
    {
        currEntry = entry;
        conditionStack = ItemStack.EMPTY;
        conditionTagField.setValue("");

        conditionTagField.visible = entry.getCondition().needsTag();
    }

    private void onConfirm()
    {
        ITextComponent error = null;
        if (currEntry == null)
        {
            error = MSG_NO_CONDITION;
        }
        else
        {
            Condition condition = currEntry.getCondition();
            if ((condition.needsItem() && conditionStack.isEmpty()) || (condition.needsTag() && conditionTagField.getValue().isEmpty()))
            {
                error = MSG_MISSING_DATA;
            }
            else if (condition.needsItem())
            {
                error = condition.validateStack(conditionStack);
            }
            else if (condition.needsTag())
            {
                error = condition.validateTag(conditionTagField.getValue());
            }
        }

        if (error != null)
        {
            mc().pushGuiLayer(MessageScreen.error(error));
            return;
        }

        parent.setCriterion(currEntry.getCondition(), conditionStack, conditionTagField.getValue());
        onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void onClose()
    {
        //Can't use the standard version of ContainerScreen#onClose() because it completely closes the Screen stack
        player().inventory.setCarried(ItemStack.EMPTY);
        player().containerMenu = parent.getMenu();
        mc().popGuiLayer();
    }

    private Minecraft mc()
    {
        if (minecraft == null) { throw new IllegalStateException("Minecraft not initialized!"); }
        return minecraft;
    }

    private PlayerEntity player()
    {
        if (mc().player == null) { throw new IllegalStateException("Minecraft#player not initialized!"); }
        return mc().player;
    }



    private static class ConditionEntry extends SelectionWidget.SelectionEntry
    {
        private final Condition condition;

        public ConditionEntry(Condition condition)
        {
            super(condition.getName());
            this.condition = condition;
        }

        public Condition getCondition() { return condition; }
    }

    protected static class DummyContainer extends Container
    {
        protected DummyContainer()
        {
            super(null, -1);

            //noinspection ConstantConditions
            PlayerInventory playerInv = Minecraft.getInstance().player.inventory;

            int yTop = MAIN_HEIGHT;
            for(int row = 0; row < 3; ++row)
            {
                for(int column = 0; column < 9; ++column)
                {
                    addSlot(new Slot(playerInv, column + row * 9 + 9, 8 + column * 18, yTop + row * 18));
                }
            }

            yTop += (18 * 3) + 4;
            for(int column = 0; column < 9; ++column)
            {
                addSlot(new Slot(playerInv, column, 8 + column * 18, yTop));
            }
        }

        @Override
        public boolean stillValid(PlayerEntity player) { return true; }
    }
}