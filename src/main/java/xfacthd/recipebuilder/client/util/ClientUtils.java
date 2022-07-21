package xfacthd.recipebuilder.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
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

    public static void drawScreenBackground(Screen screen, MatrixStack mstack, int screenX, int screenY, int screenWidth, int screenHeight)
    {
        Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
        drawNineSliceTexture(screen, mstack, screenX, screenY, screenWidth, screenHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BORDER);
    }

    public static void drawBuilderBackground(Screen screen, MatrixStack mstack, int screenX, int screenY, int screenWidth, int screenHeight)
    {
        Minecraft.getInstance().textureManager.bind(BUILDER_BACKGROUND_TEXTURE);
        drawNineSliceTexture(screen, mstack, screenX, screenY, screenWidth, screenHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BUILDER_BORDER);
    }

    public static void drawNineSliceTexture(Screen screen, MatrixStack mstack, int screenX, int screenY, int screenWidth, int screenHeight, int texWidth, int texHeight, int border)
    {
        int texCenterWidth = texWidth - (border * 2);
        int texCenterHeight = texHeight - (border * 2);

        TextureDrawer.start();

        //Corners
        TextureDrawer.fillGuiBuffer(mstack, screen, screenX, screenY, 0, 0, border, border);
        TextureDrawer.fillGuiBuffer(mstack, screen, screenX + screenWidth - border, screenY, texWidth - border, 0, border, border);
        TextureDrawer.fillGuiBuffer(mstack, screen, screenX, screenY + screenHeight - border, 0, texHeight - border, border, border);
        TextureDrawer.fillGuiBuffer(mstack, screen, screenX + screenWidth - border, screenY + screenHeight - border, texWidth - border, texHeight - border, border, border);

        //Edges
        for (int i = 0; i <= (screenWidth / texCenterWidth); i++)
        {
            int x = screenX + border + (i * texCenterWidth);
            int width = Math.min(texCenterWidth, screenWidth - (i * texCenterWidth) - (border * 2));
            TextureDrawer.fillGuiBuffer(mstack, screen, x, screenY, border, 0, width, border);
            TextureDrawer.fillGuiBuffer(mstack, screen, x, screenY + screenHeight - border, border, texHeight - border, width, border);
        }
        for (int i = 0; i <= (screenHeight / texCenterHeight); i++)
        {
            int y = screenY + border + (i * texCenterHeight);
            int height = Math.min(texCenterHeight, screenHeight - (i * texCenterHeight) - (border * 2));
            TextureDrawer.fillGuiBuffer(mstack, screen, screenX, y, 0, border, border, height);
            TextureDrawer.fillGuiBuffer(mstack, screen, screenX + screenWidth - border, y, texWidth - border, border, border, height);
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
                TextureDrawer.fillGuiBuffer(mstack, screen, x, y, border, border, width, height);
            }
        }

        TextureDrawer.end();
    }

    public static void drawInventoryBackground(Screen screen, MatrixStack mstack, int invX, int invY, boolean withBorder)
    {
        Minecraft.getInstance().textureManager.bind(INVENTORY_TEXTURE);

        if (withBorder)
        {
            screen.blit(mstack, invX, invY, 0, INVENTORY_TEX_Y, INVENTORY_WIDTH_BORDER, INVENTORY_HEIGHT_BORDER);
        }
        else
        {
            screen.blit(mstack, invX, invY, INVENTORY_TEX_X, INVENTORY_TEX_Y, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        }
    }

    public static void drawSlotBackground(Screen screen, MatrixStack mstack, int slotX, int slotY)
    {
        Minecraft.getInstance().textureManager.bind(INVENTORY_TEXTURE);
        screen.blit(mstack, slotX, slotY, SLOT_TEX_X, SLOT_TEX_Y, SLOT_SIZE, SLOT_SIZE);
    }

    //Copy of `FontRenderer.drawWordWrap()` for use with a MatrixStack
    public static int drawWordWrap(FontRenderer font, MatrixStack mstack, ITextProperties text, int x, int y, int width, int color)
    {
        for (IReorderingProcessor line : font.split(text, width))
        {
            font.draw(mstack, line, x, y, color);
            y += font.lineHeight;
        }
        return y;
    }

    public static int getWrappedHeight(FontRenderer font, ITextProperties text, int width)
    {
        return font.split(text, width).size() * font.lineHeight;
    }
}