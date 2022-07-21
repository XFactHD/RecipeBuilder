package xfacthd.recipebuilder.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ObjectUtils
{
    public static ITextComponent getItemTranslation(String name)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return item.getName(item.getDefaultInstance());
    }

    public static ITextComponent getFluidTranslation(String name)
    {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return fluid.getAttributes().getDisplayName(new FluidStack(fluid, 1));
    }

    public static TextureAtlasSprite getStillSprite(Fluid fluid)
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
