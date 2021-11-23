package xfacthd.recipebuilder.client.data.slots;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import xfacthd.recipebuilder.client.data.SlotContent;

public class FloatSlot extends NumberSlot<FloatSlot.FloatContent>
{
    public FloatSlot(String name, boolean optional, ITextComponent title) { super(name, optional, title); }

    @Override
    public FloatContent newEmptyContent() { return new FloatContent(0F); }

    @Override
    public void renderContent(Screen screen, FloatContent content, MatrixStack mstack, int builderX, int builderY, int blitBase, FontRenderer font) { }

    @Override
    public void renderTooltip(Screen screen, FloatContent content, MatrixStack mstack, int mouseX, int mouseY, FontRenderer font) { }

    public static class FloatContent extends SlotContent<Float> implements INumberContent
    {
        protected FloatContent(Float content) { super(content); }

        @Override
        public void acceptItem(ItemStack stack) { }

        @Override
        public void clear() { setContent(0F); }

        @Override
        public boolean isEmpty() { return content == 0; }
    }
}