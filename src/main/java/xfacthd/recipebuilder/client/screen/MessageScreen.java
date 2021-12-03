package xfacthd.recipebuilder.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.*;
import xfacthd.recipebuilder.client.util.ClientUtils;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;

public class MessageScreen extends Screen
{
    public static final ITextComponent INFO_TITLE = Utils.translate(null, "message.info.title");
    public static final ITextComponent ERROR_TITLE = Utils.translate(null, "message.error.title");
    public static final ITextComponent TITLE_BTN_OK = Utils.translate(null, "message.btn.ok");
    private static final int WIDTH = 176;
    private static final int BASE_HEIGHT = 64;
    private static final int TEXT_WIDTH = WIDTH - 12;
    private static final int TITLE_X = 8;
    private static final int TITLE_Y = 6;

    private final List<ITextComponent> messages;
    private final List<List<IReorderingProcessor>> textBlocks = new ArrayList<>();
    private int leftPos;
    private int topPos;
    private int imageHeight;

    public static MessageScreen info(ITextComponent message) { return new MessageScreen(INFO_TITLE, Collections.singletonList(message)); }

    public static MessageScreen info(List<ITextComponent> message) { return new MessageScreen(INFO_TITLE, message); }

    public static MessageScreen error(ITextComponent message) { return new MessageScreen(ERROR_TITLE, Collections.singletonList(message)); }

    public static MessageScreen error(List<ITextComponent> message) { return new MessageScreen(ERROR_TITLE, message); }

    public MessageScreen(ITextComponent title, List<ITextComponent> messages)
    {
        super(title);
        this.messages = messages;
    }

    @Override
    protected void init()
    {
        textBlocks.clear();

        imageHeight = BASE_HEIGHT;
        for (ITextComponent msg : messages)
        {
            imageHeight += ClientUtils.getWrappedHeight(font, msg, TEXT_WIDTH);
            imageHeight += font.lineHeight;

            textBlocks.add(font.split(msg, TEXT_WIDTH));
        }
        imageHeight -= font.lineHeight;

        leftPos = (this.width - WIDTH) / 2;
        topPos = (this.height - imageHeight) / 2;

        addButton(new Button(leftPos + (WIDTH / 2) - 30, topPos + imageHeight - 30, 60, 20, TITLE_BTN_OK, btn -> onClose()));
    }

    @Override
    public void render(MatrixStack mstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(mstack);

        ClientUtils.drawScreenBackground(this, mstack, leftPos, topPos, WIDTH, imageHeight);
        font.draw(mstack, title, leftPos + TITLE_X, topPos + TITLE_Y, 0x404040);

        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<IReorderingProcessor> block : textBlocks)
        {
            for (IReorderingProcessor line : block)
            {
                font.draw(mstack, line, leftPos + TITLE_X, y, 0);
                y += font.lineHeight;
            }
            y += font.lineHeight;
        }

        Style style = findTextLine(mouseX, mouseY);
        if (style != null)
        {
            renderComponentHoverEffect(mstack, style, mouseX, mouseY);
        }

        super.render(mstack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        Style style = findTextLine((int) mouseX, (int) mouseY);
        if (style != null && handleComponentClicked(style))
        {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Style findTextLine(int mouseX, int mouseY)
    {
        int localX = mouseX - leftPos - TITLE_X;
        if (localX < 0) { return null; }

        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<IReorderingProcessor> block : textBlocks)
        {
            int height = block.size() * font.lineHeight;
            if (mouseY >= y && mouseY <= y + height)
            {
                int idx = (mouseY - y) / font.lineHeight;
                if (idx >= block.size()) { return null; }
                return font.getSplitter().componentStyleAtWidth(block.get(idx), localX);
            }

            y += height + font.lineHeight;
        }
        return null;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}