package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
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
    public void render(PoseStack pstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
        GuiComponent.blit(pstack, left + 2, top + 6, parent.getBlitOffset(), 16, 16, sprite);

        super.render(pstack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTicks);
    }



    private static Component getTranslation(String name)
    {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return fluid.getAttributes().getDisplayName(new FluidStack(fluid, 1));
    }

    private static TextureAtlasSprite getStillSprite(Fluid fluid)
    {
        FluidAttributes attr = fluid.getAttributes();
        //noinspection deprecation
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(attr.getStillTexture());
    }

    public static float[] getFluidColor(Fluid fluid)
    {
        int color = fluid.getAttributes().getColor();
        return new float[] {
                (float) FastColor.ARGB32.red(color) / 255F,
                (float) FastColor.ARGB32.green(color) / 255F,
                (float) FastColor.ARGB32.blue(color) / 255F,
                (float) FastColor.ARGB32.alpha(color) / 255F
        };
    }
}