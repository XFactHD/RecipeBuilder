package xfacthd.recipebuilder.client.screen.edit;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.recipebuilder.client.data.slots.FluidSlot;
import xfacthd.recipebuilder.client.screen.EditSlotScreen;
import xfacthd.recipebuilder.client.screen.widget.*;
import xfacthd.recipebuilder.client.util.ObjectUtils;
import xfacthd.recipebuilder.common.util.Utils;

public class EditFluidSlotScreen extends EditSlotScreen<FluidStack, FluidSlot.FluidContent, FluidSlot>
{
    public static final ITextComponent TITLE = Utils.translate(null, "edit_fluid.title");
    public static final ITextComponent TITLE_AMOUNT = Utils.translate(null, "edit_fluid.amount.title");

    private FluidAmountEditBox amountEdit = null;

    public EditFluidSlotScreen(FluidSlot slot, FluidSlot.FluidContent content)
    {
        super(TITLE, slot, content);
        additionalHeight = 23;
    }

    @Override
    protected void addAdditionalWidgets()
    {
        FluidAmountEditBox edit = new FluidAmountEditBox(font, getLeftPos() + 50, getTopPosAdditional(), 117);
        if (amountEdit != null)
        {
            edit.setValue(amountEdit.getValue());
        }
        amountEdit = addButton(edit);
    }

    @Override
    protected void renderContent(MatrixStack pstack, int slotX, int slotY, int mouseX, int mouseY)
    {
        //noinspection deprecation
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        RenderSystem.enableBlend();

        Fluid fluid = content.getContent().getFluid();
        ObjectUtils.useFluidForShaderColor(fluid);
        TextureAtlasSprite sprite = ObjectUtils.getStillSprite(fluid);

        AbstractGui.blit(pstack, slotX, slotY, getBlitOffset(), 16, 16, sprite);

        //noinspection deprecation
        RenderSystem.color4f(1, 1, 1, 1);
    }

    @Override
    protected void renderAdditional(MatrixStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        drawString(pstack, font, TITLE_AMOUNT, getLeftPos() + LEFT_OFFSET, getTopPosAdditional() + 5, 0xFFE0E0E0);
    }

    @Override
    protected void gatherTags(FluidStack content, SelectionWidget<LocationEntry> widget)
    {
        content.getFluid().getTags().forEach(tag -> widget.addEntry(new LocationEntry(tag)));
    }

    @Override
    protected void onConfirm()
    {
        amountEdit.commit();
        super.onConfirm();
    }



    private class FluidAmountEditBox extends NumberTextFieldWidget
    {
        private final FluidStack fluid;

        public FluidAmountEditBox(FontRenderer font, int x, int y, int width)
        {
            super(font, x, y, width, 18, new StringTextComponent(""), false);
            this.fluid = content.getContent();
            setValue(Integer.toString(fluid.getAmount()));
            setFilter(INTEGER_NON_ZERO_FILTER);
        }

        @Override
        public void commit()
        {
            int value = getIntegerValue();
            fluid.setAmount(Math.min(value, slot.getTankSize()));
        }
    }
}
