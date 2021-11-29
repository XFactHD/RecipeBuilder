package xfacthd.recipebuilder.client.screen.widget.taglist;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class NoIconTagEntry extends AbstractTagEntry
{
    private NoIconTagEntry(String name, ITextComponent translatedName) { super(name, translatedName, 0); }

    public static NoIconTagEntry entity(String name)
    {
        EntityType<?> entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, entry.getDescription());
    }

    public static NoIconTagEntry tileEntity(String name)
    {
        TileEntityType<?> entry = ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, new TranslationTextComponent(entry.getRegistryName().toString()));
    }

    public static NoIconTagEntry potion(String name)
    {
        Effect entry = ForgeRegistries.POTIONS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, entry.getDisplayName());
    }

    public static NoIconTagEntry enchantment(String name)
    {
        Enchantment entry = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, new TranslationTextComponent(entry.getDescriptionId()));
    }
}