package xfacthd.recipebuilder.client.screen.widget.taglist;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
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
    public void render(PoseStack pstack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks)
    {
        //noinspection deprecation
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
        GuiComponent.blit(pstack, left + 2, top + 6, parent.getBlitOffset(), 16, 16, sprite);

        super.render(pstack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTicks);
    }
}