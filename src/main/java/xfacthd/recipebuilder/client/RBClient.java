package xfacthd.recipebuilder.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import org.lwjgl.glfw.GLFW;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.builders.vanilla.*;
import xfacthd.recipebuilder.client.screen.RecipeBuilderScreen;
import xfacthd.recipebuilder.client.data.AbstractBuilder;
import xfacthd.recipebuilder.client.screen.TagBuilderScreen;
import xfacthd.recipebuilder.common.net.PacketOpenRecipeBuilder;
import xfacthd.recipebuilder.common.net.PacketOpenTagBuilder;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RecipeBuilder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RBClient
{
    public static final Map<RecipeSerializer<?>, AbstractBuilder> BUILDERS = new Object2ObjectArrayMap<>();

    public static final Lazy<KeyMapping> KEY_BIND_OPEN_RECIPE_BUILDER = makeKeyBind("recipebuilder.key.open_recipe_builder", GLFW.GLFW_KEY_B);
    public static final Lazy<KeyMapping> KEY_BIND_OPEN_TAG_BUILDER = makeKeyBind("recipebuilder.key.open_tag_builder", GLFW.GLFW_KEY_V);

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ClientRegistry.registerKeyBinding(KEY_BIND_OPEN_RECIPE_BUILDER.get());
            ClientRegistry.registerKeyBinding(KEY_BIND_OPEN_TAG_BUILDER.get());

            MenuScreens.register(RecipeBuilder.RECIPE_BUILDER_CONTAINER.get(), RecipeBuilderScreen::new);
            MenuScreens.register(RecipeBuilder.TAG_BUILDER_CONTAINER.get(), TagBuilderScreen::new);
        });

        MinecraftForge.EVENT_BUS.addListener(RBClient::onMainMenuOpen);
        MinecraftForge.EVENT_BUS.addListener(RBClient::onClientTickStart);

        BUILDERS.put(RecipeSerializer.SHAPED_RECIPE, new ShapedCraftingBuilder());
        BUILDERS.put(RecipeSerializer.SHAPELESS_RECIPE, new ShapelessCraftingBuilder());
        BUILDERS.put(RecipeSerializer.SMELTING_RECIPE, new CookingBuilder(RecipeSerializer.SMELTING_RECIPE, new ItemStack(Blocks.FURNACE), 200));
        BUILDERS.put(RecipeSerializer.BLASTING_RECIPE, new CookingBuilder(RecipeSerializer.BLASTING_RECIPE, new ItemStack(Blocks.BLAST_FURNACE), 100));
        BUILDERS.put(RecipeSerializer.SMOKING_RECIPE, new CookingBuilder(RecipeSerializer.SMOKING_RECIPE, new ItemStack(Blocks.SMOKER), 100));
        BUILDERS.put(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, new CookingBuilder(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, new ItemStack(Blocks.CAMPFIRE), 100, 9));
        BUILDERS.put(RecipeSerializer.STONECUTTER, new StonecuttingBuilder());
        BUILDERS.put(RecipeSerializer.SMITHING, new SmithingBuilder());
    }

    @SubscribeEvent
    public static void onHandleIMC(final InterModProcessEvent event)
    {
        Map<String, List<AbstractBuilder>> buildersPerMod = new Object2ObjectArrayMap<>();

        event.getIMCStream("builder"::equals).forEach(msg ->
        {
            Supplier<AbstractBuilder> builder = msg.getMessageSupplier();
            buildersPerMod.computeIfAbsent(
                    msg.getSenderModId(),
                    modid -> new ArrayList<>()
            ).add(builder.get());

            RecipeBuilder.LOGGER.error("Received builder via IMC message from mod '{}'", msg.getSenderModId());
        });

        buildersPerMod.keySet()
                .stream()
                .sorted()
                .map(buildersPerMod::get)
                .flatMap(List::stream)
                .forEach(builder -> BUILDERS.put(builder.getType(), builder));
    }

    private static void onMainMenuOpen(final ScreenEvent.InitScreenEvent.Pre event)
    {
        if (event.getScreen() instanceof TitleScreen)
        {
            Utils.enteredMainMenu();
        }
    }

    private static void onClientTickStart(final TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START || Minecraft.getInstance().screen != null) { return; }

        if (KEY_BIND_OPEN_RECIPE_BUILDER.get().consumeClick())
        {
            RecipeBuilder.NETWORK.sendToServer(new PacketOpenRecipeBuilder());
        }

        if (KEY_BIND_OPEN_TAG_BUILDER.get().consumeClick())
        {
            RecipeBuilder.NETWORK.sendToServer(new PacketOpenTagBuilder());
        }
    }

    private static Lazy<KeyMapping> makeKeyBind(String name, int key)
    {
        return Lazy.of(() -> new KeyMapping(name, key, "key.categories.recipebuilder"));
    }
}