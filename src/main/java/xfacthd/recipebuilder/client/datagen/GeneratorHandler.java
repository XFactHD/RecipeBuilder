package xfacthd.recipebuilder.client.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.datagen.providers.*;

@Mod.EventBusSubscriber(modid = RecipeBuilder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(new EnglishLangProvider(generator));
    }
}