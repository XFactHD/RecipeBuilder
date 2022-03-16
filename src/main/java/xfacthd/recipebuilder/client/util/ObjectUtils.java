package xfacthd.recipebuilder.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ObjectUtils
{
    public static Component getItemTranslation(String name)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return item.getName(item.getDefaultInstance());
    }

    public static Component getFluidTranslation(String name)
    {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return fluid.getAttributes().getDisplayName(new FluidStack(fluid, 1));
    }

    public static TextureAtlasSprite getStillSprite(Fluid fluid)
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

    public static void useFluidForShaderColor(Fluid fluid)
    {
        int color = fluid.getAttributes().getColor();
        RenderSystem.setShaderColor(
                (float) FastColor.ARGB32.red(color) / 255F,
                (float) FastColor.ARGB32.green(color) / 255F,
                (float) FastColor.ARGB32.blue(color) / 255F,
                (float) FastColor.ARGB32.alpha(color) / 255F
        );
    }
}
