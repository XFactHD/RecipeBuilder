package xfacthd.recipebuilder.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class ScissoredList<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T>
{
    protected final int listWidth;

    public ScissoredList(Minecraft mc, int width, int height, int top, int bottom, int entryHeight)
    {
        super(mc, width, height, top, bottom, entryHeight);
        this.listWidth = width;
    }

    @Override
    public void render(PoseStack pstack, int pMouseX, int pMouseY, float pPartialTicks)
    {
        int windowHeight = minecraft.getWindow().getGuiScaledHeight();
        double scale = minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor(
                (int)(x0 * scale),
                (int)((windowHeight - y1 - 1) * scale),
                (int)((listWidth + 6) * scale),
                (int)(height * scale)
        );

        super.render(pstack, pMouseX, pMouseY, pPartialTicks);

        RenderSystem.disableScissor();

        renderTooltips(pstack, pMouseX, pMouseY, pPartialTicks);
    }

    protected void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) { }
}