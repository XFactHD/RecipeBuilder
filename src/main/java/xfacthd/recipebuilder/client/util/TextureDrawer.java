package xfacthd.recipebuilder.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class TextureDrawer
{
    private static BufferBuilder buffer;

    /**
     * Draw a texture with arbitrary dimensions
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawTexture(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a texture with arbitrary dimensions
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawTexture(float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(x, y, z, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a texture with arbitrary dimensions
     * @param pstack The MatrixStack of the current context
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawTexture(MatrixStack pstack, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(pstack, x, y, 0, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a texture with arbitrary dimensions
     * @param pstack The MatrixStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawTexture(MatrixStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a tinted texture with arbitrary dimensions
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void drawTexture(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        startColored();
        fillBuffer(x, y, w, h, minU, maxU, minV, maxV, color);
        end();
    }

    /**
     * Draw a texture with a size of 256x256 in a gui
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     */
    public static void drawGuiTexture(Screen gui, float x, float y, float texX, float texY, float w, float h)
    {
        start();
        fillGuiBuffer(gui, x, y, texX, texY, w, h);
        end();
    }

    /**
     * Draw a tinted texture with a size of 256x256 in a gui
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     * @param color Color to tint the texture in
     */
    public static void drawGuiTexture(Screen gui, float x, float y, float texX, float texY, float w, float h, int color)
    {
        startColored();
        fillGuiBuffer(gui, x, y, texX, texY, w, h, color);
        end();
    }

    /**
     * Draw a texture with arbitrary dimensions in a gui
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawGuiTexture(Screen gui, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillGuiBuffer(gui, x, y, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a tinted texture with arbitrary dimensions in a gui
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void drawGuiTexture(Screen gui, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        startColored();
        fillGuiBuffer(gui, x, y, w, h, minU, maxU, minV, maxV, color);
        end();
    }

    /**
     * Start drawing textures to a buffer<br>
     * Call before using {@link TextureDrawer#fillBuffer}
     */
    public static void start()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    }

    /**
     * Start drawing tinted textures to a buffer<br>
     * Call before using {@link TextureDrawer#fillBuffer}
     */
    public static void startColored()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        buffer = Tessellator.getInstance().getBuilder();
        //noinspection deprecation
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    }

    /**
     * Fill the draw buffer with a texture with arbitrary dimensions
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void fillBuffer(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        fillBuffer(x, y, 0, w, h, minU, maxU, minV, maxV);
    }

    /**
     * Fill the draw buffer with a tinted texture with arbitrary dimensions
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillBuffer(float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        fillBuffer(x, y, 0, w, h, minU, maxU, minV, maxV, color);
    }

    /**
     * Fill the draw buffer for a gui with a texture with a size of 256x256
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     */
    public static void fillGuiBuffer(Screen gui, float x, float y, float texX, float texY, float w, float h)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(x, y, gui.getBlitOffset(), w, h, minU, maxU, minV, maxV);
    }

    /**
     * Fill the draw buffer for a gui with a tinted texture with a size of 256x256
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillGuiBuffer(Screen gui, float x, float y, float texX, float texY, float w, float h, int color)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(x, y, gui.getBlitOffset(), w, h, minU, maxU, minV, maxV, color);
    }

    /**
     * Fill the draw buffer for a gui with a texture with arbitrary dimensions
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void fillGuiBuffer(Screen gui, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        fillBuffer(x, y, gui.getBlitOffset(), w, h, minU, maxU, minV, maxV);
    }

    /**
     * Fill the draw buffer for a gui with a tinted texture with arbitrary dimensions
     * @param gui The gui to draw into
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillGuiBuffer(Screen gui, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        fillBuffer(x, y, gui.getBlitOffset(), w, h, minU, maxU, minV, maxV, color);
    }

    /**
     * Fill the draw buffer with a texture with arbitrary dimensions
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void fillBuffer(float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.vertex(x,     y + h, z).uv(minU, maxV).endVertex();
        buffer.vertex(x + w, y + h, z).uv(maxU, maxV).endVertex();
        buffer.vertex(x + w, y,     z).uv(maxU, minV).endVertex();
        buffer.vertex(x,     y,     z).uv(minU, minV).endVertex();
    }

    /**
     * Fill the draw buffer with a texture with arbitrary dimensions
     * @param pstack The MatrixStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void fillBuffer(MatrixStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.vertex(pstack.last().pose(), x,     y + h, z).uv(minU, maxV).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y + h, z).uv(maxU, maxV).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y,     z).uv(maxU, minV).endVertex();
        buffer.vertex(pstack.last().pose(), x,     y,     z).uv(minU, minV).endVertex();
    }

    /**
     * Fill the draw buffer with a tinted texture with arbitrary dimensions
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillBuffer(float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        int[] colors = getRGBAArrayFromHexColor(color);
        buffer.vertex(x,     y + h, z).uv(minU, maxV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(x + w, y + h, z).uv(maxU, maxV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(x + w, y,     z).uv(maxU, minV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(x,     y,     z).uv(minU, minV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
    }

    /**
     * Fill the draw buffer with a tinted texture with arbitrary dimensions
     * @param pstack The MatrixStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillBuffer(MatrixStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        int[] colors = getRGBAArrayFromHexColor(color);
        buffer.vertex(pstack.last().pose(), x,     y + h, z).uv(minU, maxV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y + h, z).uv(maxU, maxV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y,     z).uv(maxU, minV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x,     y,     z).uv(minU, minV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
    }

    /**
     * Finish drawing textures to a buffer<br>
     * Call after {@link TextureDrawer#start} and one or more {@link TextureDrawer#fillBuffer} calls
     */
    public static void end()
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.end();
        WorldVertexBufferUploader.end(buffer);

        buffer = null;
    }

    private static int[] getRGBAArrayFromHexColor(int color)
    {
        int[] ints = new int[4];
        ints[0] = (color >> 24 & 255);
        ints[1] = (color >> 16 & 255);
        ints[2] = (color >>  8 & 255);
        ints[3] = (color       & 255);
        return ints;
    }
}