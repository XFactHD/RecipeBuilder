package xfacthd.recipebuilder.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import xfacthd.recipebuilder.client.data.Condition;
import xfacthd.recipebuilder.client.screen.widget.HintedTextFieldWidget;
import xfacthd.recipebuilder.client.screen.widget.SelectionWidget;
import xfacthd.recipebuilder.client.util.ClientUtils;
import xfacthd.recipebuilder.common.container.RecipeBuilderContainer;
import xfacthd.recipebuilder.common.util.Utils;

public class SelectConditionScreen extends AbstractContainerScreen<RecipeBuilderContainer>
{
    public static final Component TITLE = Utils.translate(null, "select_condition.title");
    public static final Component TITLE_SELECT = Utils.translate(null, "select_condition.select.title");
    public static final Component TITLE_STACK = Utils.translate(null, "select_condition.item.title");
    public static final Component TITLE_TAG = Utils.translate(null, "select_condition.tag.title");
    public static final Component MSG_NO_CONDITION = Utils.translate("msg", "select_condition.no_condition");
    public static final Component MSG_MISSING_DATA = Utils.translate("msg", "select_condition.missing_data");
    private static final int WIDTH = 176;
    private static final int INV_HEIGHT = ClientUtils.INVENTORY_HEIGHT;
    private static final int LEFT_OFFSET = 8;
    private static final int SLOT_X = 70;
    private static final int SLOT_Y = 50;

    private final RecipeBuilderScreen parent;
    private final int localWidth;
    private int localLeft;
    private SelectionWidget<ConditionEntry> selection = null;
    private ConditionEntry currEntry = null;
    private ItemStack conditionStack = ItemStack.EMPTY;
    private EditBox conditionTagField = null;

    public SelectConditionScreen(RecipeBuilderScreen parent)
    {
        //noinspection ConstantConditions
        super(parent.getMenu(), Minecraft.getInstance().player.getInventory(), TITLE);
        this.parent = parent;
        this.imageWidth = RecipeBuilderScreen.WIDTH;
        this.imageHeight = RecipeBuilderScreen.HEIGHT;
        this.localWidth = WIDTH;
    }

    @Override
    protected void init()
    {
        super.init();

        this.localLeft = (this.imageWidth - this.localWidth) / 2 + leftPos;

        int edgeDistX = (RecipeBuilderScreen.WIDTH - WIDTH) / 2;
        titleLabelX += edgeDistX;
        inventoryLabelX += edgeDistX;
        inventoryLabelY = RecipeBuilderScreen.HEIGHT - 4 - INV_HEIGHT - font.lineHeight + 2;

        selection = addWidget(new SelectionWidget<>(localLeft + LEFT_OFFSET, topPos + 20, WIDTH - 16, TITLE_SELECT, this::onEntrySelected));
        selection.addEntry(new ConditionEntry(Condition.HAS_ITEM));
        selection.addEntry(new ConditionEntry(Condition.HAS_ITEM_TAG));
        selection.addEntry(new ConditionEntry(Condition.ENTERED_BLOCK));
        if (currEntry != null)
        {
            selection.setSelected(currEntry, false);
        }

        conditionTagField = addRenderableWidget(new HintedTextFieldWidget(font, localLeft + LEFT_OFFSET + 1, topPos + 50, WIDTH - 18, 18, conditionTagField, TITLE_TAG));
        conditionTagField.visible = false;

        addRenderableWidget(new Button(localLeft + (WIDTH / 2) - 30, topPos + RecipeBuilderScreen.HEIGHT - INV_HEIGHT - 40, 60, 20, MessageScreen.TITLE_BTN_OK, btn -> onConfirm()));
    }

    @Override
    public void render(PoseStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(pstack);
        super.render(pstack, mouseX, mouseY, partialTicks);
        selection.render(pstack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(PoseStack pstack, float partialTicks, int mouseX, int mouseY)
    {
        ClientUtils.drawScreenBackground(this, pstack, localLeft, topPos, WIDTH, RecipeBuilderScreen.HEIGHT);
        ClientUtils.drawInventoryBackground(this, pstack, localLeft, topPos + RecipeBuilderScreen.HEIGHT - INV_HEIGHT - 4, true);

        if (currEntry != null && currEntry.getCondition().needsItem())
        {
            font.draw(pstack, TITLE_STACK, localLeft + LEFT_OFFSET, topPos + 55, 0x404040);
            renderConditionSlot(pstack, mouseX, mouseY, localLeft + SLOT_X, topPos + SLOT_Y);
        }
    }

    @Override
    protected void renderFloatingItem(ItemStack stack, int x, int y, String countTag)
    {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(0, 0, 2000);
        RenderSystem.applyModelViewMatrix();

        super.renderFloatingItem(stack, x, y, countTag);

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    protected void renderSlot(PoseStack poseStack, Slot slot)
    {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(0, 0, 2000);
        RenderSystem.applyModelViewMatrix();

        super.renderSlot(poseStack, slot);

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    protected void containerTick() { conditionTagField.tick(); }

    private void renderConditionSlot(PoseStack pstack, int mouseX, int mouseY, int slotX, int slotY)
    {
        ClientUtils.drawSlotBackground(this, pstack, slotX, slotY);
        renderSlot(pstack, mouseX, mouseY, slotX + 1, slotY + 1, conditionStack);
    }

    private void renderSlot(PoseStack pstack, int mouseX, int mouseY, int slotX, int slotY, ItemStack content)
    {
        //don't render when the slot position is below the opened selection menu
        if (selection.isMouseOver(slotX, slotY)) { return; }

        //Item
        if (!content.isEmpty())
        {
            setBlitOffset(2100);
            itemRenderer.blitOffset = 2100.0F;

            RenderSystem.enableDepthTest();
            itemRenderer.renderAndDecorateItem(content, slotX, slotY);
            itemRenderer.renderGuiItemDecorations(font, content, slotX, slotY, null);

            itemRenderer.blitOffset = 0.0F;
            setBlitOffset(0);
        }

        //Hover overlay
        if (mouseX >= slotX && mouseX <= slotX + 16 && mouseY >= slotY && mouseY <= slotY + 16)
        {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);

            fillGradient(pstack, slotX, slotY, slotX + 16, slotY + 16, 0x80ffffff, 0x80ffffff);

            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == 0 && currEntry != null && currEntry.getCondition().needsItem() && !selection.isMouseOver(mouseX, mouseY))
        {
            int slotX = localLeft + SLOT_X + 1;
            int slotY = topPos + SLOT_Y + 1;
            if (mouseX >= slotX && mouseX <= slotX + 16 && mouseY >= slotY && mouseY <= slotY + 16)
            {
                ItemStack carried = menu.getCarried();
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

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        if (mc().options.keyInventory.matches(pKeyCode, pScanCode) && conditionTagField.isFocused())
        {
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
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
        Component error = null;
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

        //noinspection ConstantConditions
        parent.setCriterion(currEntry.getCondition(), conditionStack, conditionTagField.getValue());
        onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    //Can't use the standard version of ContainerScreen#onClose() and ContainerScreen#removed() because
    //they completely close the Screen stack and kill the container
    @Override
    public void onClose()
    {
        player().containerMenu = parent.getMenu();
        mc().popGuiLayer();
    }

    @Override
    public void removed() { }

    private Minecraft mc()
    {
        if (minecraft == null) { throw new IllegalStateException("Minecraft not initialized!"); }
        return minecraft;
    }

    private Player player()
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
}