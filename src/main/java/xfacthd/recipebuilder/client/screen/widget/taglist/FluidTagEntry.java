package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.recipebuilder.client.util.ObjectUtils;

public class FluidTagEntry extends AbstractTagEntry
{
    private final TextureAtlasSprite sprite;
    private final float[] color;

    public FluidTagEntry(String name)
    {
        super(name, ObjectUtils.getFluidTranslation(name), 18);
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        sprite = ObjectUtils.getStillSprite(fluid);
        color = ObjectUtils.getFluidColor(fluid);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(MatrixStack mstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        RenderSystem.color4f(color[0], color[1], color[2], color[3]);
        AbstractGui.blit(mstack, left + 2, top + 6, parent.getBlitOffset(), 16, 16, sprite);

        super.render(mstack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTicks);
    }
}