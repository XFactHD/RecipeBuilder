package xfacthd.recipebuilder.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import xfacthd.recipebuilder.client.data.slots.INumberContent;
import xfacthd.recipebuilder.client.data.slots.NumberSlot;
import xfacthd.recipebuilder.client.screen.widget.NumberSlotTextFieldWidget;
import xfacthd.recipebuilder.client.util.ClientUtils;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;

public class EditParametersScreen extends Screen
{
    public static final ITextComponent TITLE = Utils.translate(null, "edit_params.title");
    private static final int WIDTH = 176;
    private static final int BASE_HEIGHT = 64;
    private static final int TITLE_Y = 6;
    private static final int LEFT_OFFSET = 8;
    private static final int FIELD_INTERVAL = 22;

    private final Map<NumberSlot<?>, INumberContent> params;
    private final List<NumberSlotTextFieldWidget> textFields = new ArrayList<>();
    private int imageHeight;
    private int leftPos;
    private int topPos;

    public EditParametersScreen(Map<NumberSlot<?>, INumberContent> params)
    {
        super(TITLE);
        this.params = params;
    }

    @Override
    protected void init()
    {
        imageHeight = BASE_HEIGHT + (params.size() * FIELD_INTERVAL);
        leftPos = (this.width - WIDTH) / 2;
        topPos = (this.height - imageHeight) / 2;

        int fieldY = topPos + TITLE_Y + font.lineHeight + 5;
        for (Map.Entry<NumberSlot<?>, INumberContent> entry : params.entrySet())
        {
            NumberSlotTextFieldWidget widget = addButton(new NumberSlotTextFieldWidget(font, leftPos + LEFT_OFFSET, fieldY, 50, 18, entry.getKey(), entry.getValue(), false));
            textFields.add(widget);
            fieldY += FIELD_INTERVAL;
        }

        addButton(new Button(leftPos + (WIDTH / 2) - 30, topPos + imageHeight - 35, 60, 20, MessageScreen.TITLE_BTN_OK, btn -> onConfirm()));
    }

    @Override
    public final void render(MatrixStack mstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(mstack);

        ClientUtils.drawScreenBackground(this, mstack, leftPos, topPos, WIDTH, imageHeight);
        font.draw(mstack, title, leftPos + LEFT_OFFSET, topPos + TITLE_Y, 0x404040);

        int fieldY = topPos + TITLE_Y + font.lineHeight + 10;
        for (NumberSlot<?> slot : params.keySet())
        {
            font.draw(mstack, slot.getTitle(), leftPos + LEFT_OFFSET + 55, fieldY, 0x404040);
            fieldY += FIELD_INTERVAL;
        }

        super.render(mstack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() { textFields.forEach(TextFieldWidget::tick); }

    private void onConfirm()
    {
        children.stream()
                .filter(w -> w instanceof NumberSlotTextFieldWidget)
                .map(NumberSlotTextFieldWidget.class::cast)
                .forEach(NumberSlotTextFieldWidget::commit);

        onClose();
    }
}