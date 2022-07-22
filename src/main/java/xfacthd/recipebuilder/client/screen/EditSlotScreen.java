package xfacthd.recipebuilder.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import xfacthd.recipebuilder.client.data.RecipeSlot;
import xfacthd.recipebuilder.client.data.SlotContent;
import xfacthd.recipebuilder.client.screen.widget.*;
import xfacthd.recipebuilder.client.util.ClientUtils;
import xfacthd.recipebuilder.common.util.Utils;

public abstract class EditSlotScreen<B, T, C extends SlotContent<T>, S extends RecipeSlot<C, B>> extends Screen
{
    public static final Component TITLE_USE_TAG = Utils.translate(null, "edit_slot.use_tag.title");
    public static final Component MSG_NO_TAG_SELECTED = Utils.translate("msg", "edit_slot.no_tag");
    protected static final int WIDTH = 176;
    protected static final int HEIGHT = 112;
    private static final int TITLE_Y = 6;
    protected static final int LEFT_OFFSET = 8;

    protected final S slot;
    protected final C content;
    private int screenHeight;
    protected int additionalHeight = 0;
    private int leftPos;
    private int topPos;
    private int topPosAdditional;
    private int slotX;
    private int slotY;
    private Checkbox useTagCheckbox = null;
    private SelectionWidget<LocationEntry> tagSelection = null;

    protected EditSlotScreen(Component title, S slot, C content)
    {
        super(title);
        this.slot = slot;
        this.content = content;
    }

    @Override
    protected final void init()
    {
        screenHeight = HEIGHT + additionalHeight;
        leftPos = (this.width - WIDTH) / 2;
        topPos = (this.height - screenHeight) / 2;
        topPosAdditional = topPos + 66;

        slotX = leftPos + LEFT_OFFSET;
        slotY = topPos + 21;

        useTagCheckbox = addRenderableWidget(new NotifyingCheckboxButton(
                leftPos + LEFT_OFFSET + 22,
                topPos + 20,
                WIDTH - (LEFT_OFFSET * 2),
                20,
                TITLE_USE_TAG,
                content.shouldUseTag(),
                btn -> onSetCheck()
        ));

        addAdditionalWidgets();

        tagSelection = new SelectionWidget<>(leftPos + LEFT_OFFSET, topPos + 43, WIDTH - (LEFT_OFFSET * 2), SelectConditionScreen.TITLE_SELECT, null);
        gatherTags(content.getContent(), tagSelection);
        tagSelection.active = content.shouldUseTag();
        if (content.shouldUseTag())
        {
            ResourceLocation id = content.getTag().location();

            tagSelection.setSelected(
                    tagSelection.stream()
                            .filter(entry -> entry.getName().equals(id))
                            .findFirst()
                            .orElse(null),
                    false
            );
        }
        addWidget(tagSelection);

        addRenderableWidget(new Button(leftPos + (WIDTH / 2) - 30, topPos + screenHeight - 35, 60, 20, MessageScreen.TITLE_BTN_OK, btn -> onConfirm()));
    }

    @Override
    public final void render(PoseStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(pstack);

        ClientUtils.drawScreenBackground(this, pstack, leftPos, topPos, WIDTH, screenHeight);
        ClientUtils.drawSlotBackground(this, pstack, slotX, slotY);
        font.draw(pstack, title, leftPos + LEFT_OFFSET, topPos + TITLE_Y, 0x404040);

        super.render(pstack, mouseX, mouseY, partialTicks);
        renderContent(pstack, slotX + 1, slotY + 1, mouseX, mouseY);
        if (mouseX >= slotX + 1 && mouseX <= slotX + 17 && mouseY >= slotY + 1 && mouseY <= slotY + 17)
        {
            slot.renderTooltip(this, content, pstack, mouseX, mouseY, font);
        }

        renderAdditional(pstack, mouseX, mouseY, partialTicks);

        tagSelection.render(pstack, mouseX, mouseY, partialTicks);
    }

    protected void addAdditionalWidgets() { }

    protected abstract void renderContent(PoseStack pstack, int slotX, int slotY, int mouseX, int mouseY);

    protected abstract void gatherTags(T content, SelectionWidget<LocationEntry> widget);

    protected void renderAdditional(PoseStack pstack, int mouseX, int mouseY, float partialTicks) { }

    protected int getLeftPos() { return leftPos; }

    protected int getTopPosAdditional() { return topPosAdditional; }

    private void onSetCheck() { tagSelection.active = useTagCheckbox.selected(); }

    protected void onConfirm()
    {
        if (useTagCheckbox.selected() && tagSelection.getSelected() == null)
        {
            //noinspection ConstantConditions
            minecraft.pushGuiLayer(MessageScreen.error(MSG_NO_TAG_SELECTED));
            return;
        }

        if (useTagCheckbox.selected())
        {
            TagKey<?> key = TagKey.create(slot.getRegistryKey(), tagSelection.getSelected().getName());
            //noinspection unchecked
            content.setTag((TagKey<T>) key);
        }
        else
        {
            content.setTag(null);
        }

        onClose();
    }
}