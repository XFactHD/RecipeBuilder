package xfacthd.recipebuilder.client.compat;

import net.minecraftforge.fml.ModList;
import xfacthd.recipebuilder.client.data.AbstractBuilder;

import java.util.List;

public class CompatHandler
{
    public static void registerModBuilders(List<AbstractBuilder> builders)
    {
        if (loaded("extendedcrafting"))
        {
            ExtendedCraftingCompat.register(builders);
        }
    }

    private static boolean loaded(String modid) { return ModList.get().isLoaded(modid); }
}
