package xfacthd.recipebuilder.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class HintedTextFieldWidget extends TextFieldWidget
{
    private final FontRenderer font;

    public HintedTextFieldWidget(FontRenderer font, int x, int y, int width, int height, ITextComponent message)
    {
        super(font, x, y, width, height, message);
        this.font = font;
    }

    public HintedTextFieldWidget(FontRenderer font, int x, int y, int width, int height, @Nullable TextFieldWidget oldField, ITextComponent message)
    {
        super(font, x, y, width, height, oldField, message);
        this.font = font;
    }

    @Override
    public void renderButton(MatrixStack mstack, int mouseX, int mouseY, float partialTicks)
    {
        super.renderButton(mstack, mouseX, mouseY, partialTicks);

        if (getValue().isEmpty())
        {
            int textX = isBordered() ? x + 4 : x;
            int textY = isBordered() ? y + (height - 8) / 2 : y;
            font.drawShadow(mstack, getMessage(), textX, textY, 0xFF666666);
        }
    }
}