package xfacthd.recipebuilder.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class HintedTextFieldWidget extends EditBox
{
    private final Font font;

    public HintedTextFieldWidget(Font font, int x, int y, int width, int height, Component message)
    {
        super(font, x, y, width, height, message);
        this.font = font;
    }

    public HintedTextFieldWidget(Font font, int x, int y, int width, int height, @Nullable EditBox oldField, Component message)
    {
        super(font, x, y, width, height, oldField, message);
        this.font = font;
    }

    @Override
    public void renderButton(PoseStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        super.renderButton(pstack, mouseX, mouseY, partialTicks);

        if (getValue().isEmpty())
        {
            int textX = isBordered() ? x + 4 : x;
            int textY = isBordered() ? y + (height - 8) / 2 : y;
            font.drawShadow(pstack, getMessage(), textX, textY, 0xFF666666);
        }
    }
}