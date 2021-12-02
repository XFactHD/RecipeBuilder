package xfacthd.recipebuilder.client.data.slots;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xfacthd.recipebuilder.client.data.SlotContent;

public class FloatSlot extends NumberSlot<FloatSlot.FloatContent>
{
    public FloatSlot(String name, boolean optional, Component title) { super(name, optional, title); }

    @Override
    public FloatContent newEmptyContent() { return new FloatContent(0F); }

    @Override
    public void renderContent(Screen screen, FloatContent content, PoseStack pstack, int builderX, int builderY, int blitBase, Font font) { }

    @Override
    public void renderTooltip(Screen screen, FloatContent content, PoseStack pstack, int mouseX, int mouseY, Font font) { }

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