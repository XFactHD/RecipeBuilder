package xfacthd.recipebuilder.client.data.slots;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import xfacthd.recipebuilder.client.data.RecipeSlot;
import xfacthd.recipebuilder.client.data.SlotContent;
import xfacthd.recipebuilder.client.screen.edit.EditFluidSlotScreen;
import xfacthd.recipebuilder.client.util.ObjectUtils;
import xfacthd.recipebuilder.client.util.TextureDrawer;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;

public class FluidSlot extends RecipeSlot<FluidSlot.FluidContent>
{
    public static final ITextComponent AMOUNT = Utils.translate(null, "fluid_slot.amount");
    private final int tankSize;

    public FluidSlot(String name, int x, int y, int width, int height, boolean optional, boolean allowTags, int tankSize)
    {
        super(name, x, y, checkWidth(width), height, optional, true, "fluid", allowTags);
        this.tankSize = tankSize;
    }

    @Override
    public FluidContent newEmptyContent() { return new FluidContent(FluidStack.EMPTY); }

    @Override
    public boolean canEdit(FluidContent content) { return !content.isEmpty(); }

    @Override
    public Screen requestEdit(FluidContent content) { return new EditFluidSlotScreen(this, content); }

    @Override
    public void renderContent(Screen screen, FluidContent content, MatrixStack mstack, int slotX, int slotY, int blitBase, FontRenderer font)
    {
        //noinspection deprecation
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        RenderSystem.enableBlend();

        Fluid fluid = content.getContent().getFluid();
        ObjectUtils.useFluidForShaderColor(fluid);
        TextureAtlasSprite sprite = ObjectUtils.getStillSprite(fluid);

        float level = content.getContent().getAmount() / (float)tankSize;
        int fluidHeight = (int) (getHeight() * level);

        TextureDrawer.start();
        for (int i = 0; i <= (fluidHeight / 16); i++)
        {
            int height = Math.min(16, fluidHeight - (i * 16));
            int y = slotY + getHeight() - (i * 16) - height;
            TextureDrawer.fillBuffer(mstack, slotX, y, screen.getBlitOffset(), getWidth(), height, sprite.getU0(), sprite.getU(getWidth()), sprite.getV(16 - height), sprite.getV1());
        }
        TextureDrawer.end();
    }

    @Override
    public void renderTooltip(Screen screen, FluidContent content, MatrixStack mstack, int mouseX, int mouseY, FontRenderer font)
    {
        List<ITextComponent> lines = new ArrayList<>();

        FluidStack fluid = content.getContent();
        lines.add(fluid.getDisplayName());
        lines.add(new StringTextComponent("")
                .append(AMOUNT)
                .append(" ")
                .append(Integer.toString(fluid.getAmount()))
                .append("mB")
                .withStyle(TextFormatting.GRAY)
        );

        screen.renderComponentTooltip(mstack, lines, mouseX, mouseY);
    }

    public int getTankSize() { return tankSize; }

    private static int checkWidth(int width)
    {
        Preconditions.checkArgument(width <= 16, "FluidSlot width can not be larger than 16!");
        return width;
    }

    public class FluidContent extends SlotContent<FluidStack>
    {
        public FluidContent(FluidStack content) { super(content); }

        @Override
        public void acceptItem(ItemStack stack)
        {
            LazyOptional<IFluidHandlerItem> fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
            FluidStack newFluid = fluidHandler.map(handler -> handler.getFluidInTank(0)).orElse(FluidStack.EMPTY);

            if (newFluid.isEmpty())
            {
                setContent(FluidStack.EMPTY);
            }
            else if (newFluid.isFluidEqual(getContent()))
            {
                getContent().setAmount(Math.min(getContent().getAmount() + newFluid.getAmount(), tankSize));
            }
            else
            {
                FluidStack content = newFluid.copy();
                content.setAmount(Math.min(content.getAmount(), tankSize));
                setContent(content);
            }
        }

        @Override
        public void clear() { setContent(FluidStack.EMPTY); }

        @Override
        public boolean isEmpty() { return content.isEmpty(); }
    }
}