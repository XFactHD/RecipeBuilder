package xfacthd.recipebuilder.client.data.slots;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import xfacthd.recipebuilder.client.data.SlotContent;

public class IntegerSlot extends NumberSlot<IntegerSlot.IntegerContent>
{
    public IntegerSlot(String name, boolean optional, ITextComponent title) { super(name, optional, title); }

    @Override
    public IntegerContent newEmptyContent() { return new IntegerContent(0); }

    @Override
    public void renderContent(Screen screen, IntegerContent content, MatrixStack mstack, int builderX, int builderY, int blitBase, FontRenderer font) { }

    @Override
    public void renderTooltip(Screen screen, IntegerContent content, MatrixStack mstack, int mouseX, int mouseY, FontRenderer font) { }

    public static class IntegerContent extends SlotContent<Integer> implements INumberContent
    {
        protected IntegerContent(Integer content) { super(content); }

        @Override
        public void acceptItem(ItemStack stack) { }

        @Override
        public void clear() { setContent(0); }

        @Override
        public boolean isEmpty() { return content == 0; }
    }
}