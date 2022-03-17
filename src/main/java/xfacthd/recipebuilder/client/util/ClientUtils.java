package xfacthd.recipebuilder.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import xfacthd.recipebuilder.common.util.Utils;

public class ClientUtils
{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("minecraft", "textures/gui/demo_background.png");
    private static final ResourceLocation BUILDER_BACKGROUND_TEXTURE = Utils.location("textures/builder_background.png");
    public static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public static final int BORDER = 4;
    private static final int BUILDER_BORDER = 2;
    private static final int BACKGROUND_WIDTH = 248;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final int INVENTORY_TEX_X = 4;
    private static final int INVENTORY_TEX_Y = 136;
    public static final int INVENTORY_WIDTH = 168;
    public static final int INVENTORY_WIDTH_BORDER = 176;
    public static final int INVENTORY_HEIGHT = 82;
    public static final int INVENTORY_HEIGHT_BORDER = 86;
    private static final int SLOT_TEX_X = 7;
    private static final int SLOT_TEX_Y = 17;
    private static final int SLOT_SIZE = 18;

    public static void drawScreenBackground(Screen screen, PoseStack pstack, int screenX, int screenY, int screenWidth, int screenHeight)
    {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        drawNineSliceTexture(screen, pstack, screenX, screenY, screenWidth, screenHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BORDER);
    }

    public static void drawBuilderBackground(Screen screen, PoseStack pstack, int screenX, int screenY, int screenWidth, int screenHeight)
    {
        RenderSystem.setShaderTexture(0, BUILDER_BACKGROUND_TEXTURE);
        drawNineSliceTexture(screen, pstack, screenX, screenY, screenWidth, screenHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BUILDER_BORDER);
    }

    public static void drawNineSliceTexture(Screen screen, PoseStack pstack, int screenX, int screenY, int screenWidth, int screenHeight, int texWidth, int texHeight, int border)
    {
        int texCenterWidth = texWidth - (border * 2);
        int texCenterHeight = texHeight - (border * 2);

        TextureDrawer.start();

        //Corners
        TextureDrawer.fillGuiBuffer(pstack, screen, screenX, screenY, 0, 0, border, border);
        TextureDrawer.fillGuiBuffer(pstack, screen, screenX + screenWidth - border, screenY, texWidth - border, 0, border, border);
        TextureDrawer.fillGuiBuffer(pstack, screen, screenX, screenY + screenHeight - border, 0, texHeight - border, border, border);
        TextureDrawer.fillGuiBuffer(pstack, screen, screenX + screenWidth - border, screenY + screenHeight - border, texWidth - border, texHeight - border, border, border);

        //Edges
        for (int i = 0; i <= (screenWidth / texCenterWidth); i++)
        {
            int x = screenX + border + (i * texCenterWidth);
            int width = Math.min(texCenterWidth, screenWidth - (i * texCenterWidth) - (border * 2));
            TextureDrawer.fillGuiBuffer(pstack, screen, x, screenY, border, 0, width, border);
            TextureDrawer.fillGuiBuffer(pstack, screen, x, screenY + screenHeight - border, border, texHeight - border, width, border);
        }
        for (int i = 0; i <= (screenHeight / texCenterHeight); i++)
        {
            int y = screenY + border + (i * texCenterHeight);
            int height = Math.min(texCenterHeight, screenHeight - (i * texCenterHeight) - (border * 2));
            TextureDrawer.fillGuiBuffer(pstack, screen, screenX, y, 0, border, border, height);
            TextureDrawer.fillGuiBuffer(pstack, screen, screenX + screenWidth - border, y, texWidth - border, border, border, height);
        }

        //Center
        int centerWidth = (screenWidth - (border * 2)) / texCenterWidth;
        int centerHeight = (screenHeight - (border * 2)) / texCenterHeight;
        for (int ix = 0; ix <= centerWidth; ix++)
        {
            for (int iy = 0; iy <= centerHeight; iy++)
            {
                int x = screenX + border + (ix * texCenterWidth);
                int y = screenY + border + (iy * texCenterHeight);
                int width = Math.min(texCenterWidth, screenWidth - (ix * texCenterWidth) - (border * 2));
                int height = Math.min(texCenterHeight, screenHeight - (iy * texCenterHeight) - (border * 2));
                TextureDrawer.fillGuiBuffer(pstack, screen, x, y, border, border, width, height);
            }
        }

        TextureDrawer.end();
    }

    public static void drawInventoryBackground(Screen screen, PoseStack pstack, int invX, int invY, boolean withBorder)
    {
        RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);

        if (withBorder)
        {
            screen.blit(pstack, invX, invY, 0, INVENTORY_TEX_Y, INVENTORY_WIDTH_BORDER, INVENTORY_HEIGHT_BORDER);
        }
        else
        {
            screen.blit(pstack, invX, invY, INVENTORY_TEX_X, INVENTORY_TEX_Y, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        }
    }

    public static void drawSlotBackground(Screen screen, PoseStack pstack, int slotX, int slotY)
    {
        RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);
        screen.blit(pstack, slotX, slotY, SLOT_TEX_X, SLOT_TEX_Y, SLOT_SIZE, SLOT_SIZE);
    }

    //Copy of `FontRenderer.drawWordWrap()` for use with a MatrixStack
    public static int drawWordWrap(Font font, PoseStack pstack, FormattedText text, int x, int y, int width, int color)
    {
        for (FormattedCharSequence line : font.split(text, width))
        {
            font.draw(pstack, line, x, y, color);
            y += font.lineHeight;
        }
        return y;
    }

    public static int getWrappedHeight(Font font, FormattedText text, int width)
    {
        return font.split(text, width).size() * font.lineHeight;
    }

    public static void drawCenteredText(PoseStack pstack, Font font, Component text, float x, float y, int color, boolean shadow)
    {
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        float textX = x - font.width(formattedcharsequence) / 2F;
        if (shadow)
        {
            font.drawShadow(pstack, formattedcharsequence, textX, y, color);
        }
        else
        {
            font.draw(pstack, formattedcharsequence, textX, y, color);
        }
    }
}