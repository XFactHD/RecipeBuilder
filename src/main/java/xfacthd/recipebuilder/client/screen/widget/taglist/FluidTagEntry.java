package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidTagEntry extends AbstractTagEntry
{
    private final TextureAtlasSprite sprite;
    private final float[] color;

    public FluidTagEntry(String name)
    {
        super(name, getTranslation(name), 18);
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        sprite = getStillSprite(fluid);
        color = getFluidColor(fluid);
    }

    @Override //TODO: the fluid texture is too dark
    public void render(MatrixStack mstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        RenderSystem.color4f(color[0], color[1], color[2], color[3]);
        AbstractGui.blit(mstack, left + 2, top + 6, parent.getBlitOffset(), 16, 16, sprite);

        super.render(mstack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTicks);
    }



    private static ITextComponent getTranslation(String name)
    {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return fluid.getAttributes().getDisplayName(new FluidStack(fluid, 1));
    }

    private static TextureAtlasSprite getStillSprite(Fluid fluid)
    {
        FluidAttributes attr = fluid.getAttributes();
        //noinspection deprecation
        return Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(attr.getStillTexture());
    }

    public static float[] getFluidColor(Fluid fluid)
    {
        int color = fluid.getAttributes().getColor();
        return new float[] {
                (float) ColorHelper.PackedColor.red(color) / 255F,
                (float) ColorHelper.PackedColor.green(color) / 255F,
                (float) ColorHelper.PackedColor.blue(color) / 255F,
                (float) ColorHelper.PackedColor.alpha(color) / 255F
        };
    }
}