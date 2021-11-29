package xfacthd.recipebuilder.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;

public abstract class ScissoredList<T extends ExtendedList.AbstractListEntry<T>> extends ExtendedList<T>
{
    protected final int listWidth;

    public ScissoredList(Minecraft mc, int width, int height, int top, int bottom, int entryHeight)
    {
        super(mc, width, height, top, bottom, entryHeight);
        this.listWidth = width;
    }

    @Override
    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
    {
        int windowHeight = minecraft.getWindow().getGuiScaledHeight();
        double scale = minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor(
                (int)(x0 * scale),
                (int)((windowHeight - y1 - 1) * scale),
                (int)((listWidth + 6) * scale),
                (int)(height * scale)
        );

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        RenderSystem.disableScissor();
    }
}