package xfacthd.recipebuilder.client.data.slots;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import xfacthd.recipebuilder.client.data.RecipeSlot;
import xfacthd.recipebuilder.client.data.SlotContent;

public class FluidSlot extends RecipeSlot<FluidSlot.FluidContent>
{
    public FluidSlot(String name, int x, int y, int width, int height, boolean optional, boolean allowTags)
    {
        super(name, x, y, width, height, optional, true, "fluid", allowTags);
    }

    @Override
    public FluidContent newEmptyContent() { return new FluidContent(FluidStack.EMPTY); }

    @Override
    public boolean canEdit(FluidContent content) { return !content.isEmpty(); }

    @Override
    public Screen requestEdit(FluidContent content)
    {
        //TODO: implement amount edit and tag selection screen
        return null;
    }

    @Override
    public void renderContent(Screen screen, FluidContent content, MatrixStack mstack, int slotX, int slotY, int blitBase, FontRenderer font)
    {
        //TODO: implement fluid rendering
    }

    @Override
    public void renderTooltip(Screen screen, FluidContent content, MatrixStack mstack, int mouseX, int mouseY, FontRenderer font)
    {
        //TODO: implement tooltip rendering
    }

    public static class FluidContent extends SlotContent<FluidStack>
    {
        public FluidContent(FluidStack content) { super(content); }

        @Override
        public void acceptItem(ItemStack stack)
        {
            LazyOptional<IFluidHandlerItem> fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
            fluidHandler.ifPresent(handler -> setContent(handler.getFluidInTank(0).copy()));
        }

        @Override
        public void clear() { setContent(FluidStack.EMPTY); }

        @Override
        public boolean isEmpty() { return content.isEmpty(); }
    }
}