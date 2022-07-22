package xfacthd.recipebuilder.client.screen.edit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import xfacthd.recipebuilder.client.data.slots.FluidSlot;
import xfacthd.recipebuilder.client.screen.EditSlotScreen;
import xfacthd.recipebuilder.client.screen.widget.*;
import xfacthd.recipebuilder.client.util.ObjectUtils;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.stream.Stream;

public class EditFluidSlotScreen extends EditSlotScreen<Fluid, FluidStack, FluidSlot.FluidContent, FluidSlot>
{
    public static final Component TITLE = Utils.translate(null, "edit_fluid.title");
    public static final Component TITLE_AMOUNT = Utils.translate(null, "edit_fluid.amount.title");

    private FluidAmountEditBox amoundEdit = null;

    public EditFluidSlotScreen(FluidSlot slot, FluidSlot.FluidContent content)
    {
        super(TITLE, slot, content);
        additionalHeight = 23;
    }

    @Override
    protected void addAdditionalWidgets()
    {
        FluidAmountEditBox edit = new FluidAmountEditBox(font, getLeftPos() + 50, getTopPosAdditional(), 117);
        if (amoundEdit != null)
        {
            edit.setValue(amoundEdit.getValue());
        }
        amoundEdit = addRenderableWidget(edit);
    }

    @Override
    protected void renderContent(PoseStack pstack, int slotX, int slotY, int mouseX, int mouseY)
    {
        //noinspection deprecation
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();

        Fluid fluid = content.getContent().getFluid();
        ObjectUtils.useFluidForShaderColor(fluid);
        TextureAtlasSprite sprite = ObjectUtils.getStillSprite(fluid);

        GuiComponent.blit(pstack, slotX, slotY, getBlitOffset(), 16, 16, sprite);

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    protected void renderAdditional(PoseStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        drawString(pstack, font, TITLE_AMOUNT, getLeftPos() + LEFT_OFFSET, getTopPosAdditional() + 5, 0xFFE0E0E0);
    }

    @Override
    protected void gatherTags(FluidStack content, SelectionWidget<LocationEntry> widget)
    {
        //noinspection ConstantConditions
        ForgeRegistries.FLUIDS.tags()
                .getReverseTag(content.getFluid())
                .map(IReverseTag::getTagKeys)
                .orElseGet(Stream::of)
                .forEach(tag -> widget.addEntry(new LocationEntry(tag.location())));
    }

    @Override
    protected void onConfirm()
    {
        amoundEdit.commit();
        super.onConfirm();
    }

    private class FluidAmountEditBox extends NumberEditBox
    {
        private final FluidStack fluid;

        public FluidAmountEditBox(Font font, int x, int y, int width)
        {
            super(font, x, y, width, 18, new TextComponent(""), false);
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
