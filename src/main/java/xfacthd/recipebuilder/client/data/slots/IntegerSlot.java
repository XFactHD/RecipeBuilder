package xfacthd.recipebuilder.client.data.slots;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xfacthd.recipebuilder.client.data.SlotContent;

public class IntegerSlot extends NumberSlot<IntegerSlot.IntegerContent>
{
    public IntegerSlot(String name, boolean optional, Component title) { super(name, optional, title); }

    @Override
    public IntegerContent newEmptyContent() { return new IntegerContent(0); }

    @Override
    public void renderContent(Screen screen, IntegerContent content, PoseStack pstack, int builderX, int builderY, int blitBase, Font font) { }

    @Override
    public void renderTooltip(Screen screen, IntegerContent content, PoseStack pstack, int mouseX, int mouseY, Font font) { }

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