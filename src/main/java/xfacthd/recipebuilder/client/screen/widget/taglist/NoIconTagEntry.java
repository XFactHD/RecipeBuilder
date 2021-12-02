package xfacthd.recipebuilder.client.screen.widget.taglist;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class NoIconTagEntry extends AbstractTagEntry
{
    private NoIconTagEntry(String name, Component translatedName) { super(name, translatedName, 0); }

    public static NoIconTagEntry entity(String name)
    {
        EntityType<?> entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, entry.getDescription());
    }

    public static NoIconTagEntry tileEntity(String name)
    {
        BlockEntityType<?> entry = ForgeRegistries.BLOCK_ENTITIES.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, new TranslatableComponent(entry.getRegistryName().toString()));
    }

    public static NoIconTagEntry potion(String name)
    {
        MobEffect entry = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, entry.getDisplayName());
    }

    public static NoIconTagEntry enchantment(String name)
    {
        Enchantment entry = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(name));
        //noinspection ConstantConditions
        return new NoIconTagEntry(name, new TranslatableComponent(entry.getDescriptionId()));
    }
}