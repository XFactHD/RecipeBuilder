package xfacthd.recipebuilder.client;

import com.google.common.collect.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import org.lwjgl.glfw.GLFW;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.builders.vanilla.*;
import xfacthd.recipebuilder.client.compat.CompatHandler;
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
    public static final Multimap<IRecipeSerializer<?>, AbstractBuilder> BUILDERS = Multimaps.newListMultimap(new Object2ObjectArrayMap<>(), ArrayList::new);
    private static final List<AbstractBuilder> MOD_BUILDERS = new ArrayList<>();

    public static final Lazy<KeyBinding> KEY_BIND_OPEN_RECIPE_BUILDER = makeKeyBind("recipebuilder.key.open_recipe_builder", GLFW.GLFW_KEY_B);
    public static final Lazy<KeyBinding> KEY_BIND_OPEN_TAG_BUILDER = makeKeyBind("recipebuilder.key.open_tag_builder", GLFW.GLFW_KEY_V);

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ClientRegistry.registerKeyBinding(KEY_BIND_OPEN_RECIPE_BUILDER.get());
            ClientRegistry.registerKeyBinding(KEY_BIND_OPEN_TAG_BUILDER.get());

            ScreenManager.register(RecipeBuilder.RECIPE_BUILDER_CONTAINER.get(), RecipeBuilderScreen::new);
            ScreenManager.register(RecipeBuilder.TAG_BUILDER_CONTAINER.get(), TagBuilderScreen::new);
        });

        MinecraftForge.EVENT_BUS.addListener(RBClient::onMainMenuOpen);
        MinecraftForge.EVENT_BUS.addListener(RBClient::onClientTickStart);

        BUILDERS.put(IRecipeSerializer.SHAPED_RECIPE, new ShapedCraftingBuilder());
        BUILDERS.put(IRecipeSerializer.SHAPELESS_RECIPE, new ShapelessCraftingBuilder());
        BUILDERS.put(IRecipeSerializer.SMELTING_RECIPE, new CookingBuilder(IRecipeSerializer.SMELTING_RECIPE, new ItemStack(Blocks.FURNACE), 200));
        BUILDERS.put(IRecipeSerializer.BLASTING_RECIPE, new CookingBuilder(IRecipeSerializer.BLASTING_RECIPE, new ItemStack(Blocks.BLAST_FURNACE), 100));
        BUILDERS.put(IRecipeSerializer.SMOKING_RECIPE, new CookingBuilder(IRecipeSerializer.SMOKING_RECIPE, new ItemStack(Blocks.SMOKER), 100));
        BUILDERS.put(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, new CookingBuilder(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, new ItemStack(Blocks.CAMPFIRE), 100, 9));
        BUILDERS.put(IRecipeSerializer.STONECUTTER, new StonecuttingBuilder());
        BUILDERS.put(IRecipeSerializer.SMITHING, new SmithingBuilder());
    }

    @SubscribeEvent
    public static void onHandleIMC(final InterModProcessEvent event)
    {
        event.getIMCStream("builder"::equals).forEach(msg ->
        {
            Supplier<AbstractBuilder> builder = msg.getMessageSupplier();
            MOD_BUILDERS.add(builder.get());

            RecipeBuilder.LOGGER.debug("Received builder via IMC message from mod '{}'", msg.getSenderModId());
        });
    }

    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        CompatHandler.registerModBuilders(MOD_BUILDERS);

        MOD_BUILDERS.stream()
                .sorted(Comparator.comparing(AbstractBuilder::getModid))
                .forEach(builder -> BUILDERS.put(builder.getType(), builder));
    }

    private static void onMainMenuOpen(final GuiScreenEvent.InitGuiEvent.Pre event)
    {
        if (event.getGui() instanceof MainMenuScreen)
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

    private static Lazy<KeyBinding> makeKeyBind(String name, int key)
    {
        return Lazy.of(() -> new KeyBinding(name, key, "key.categories.recipebuilder"));
    }
}