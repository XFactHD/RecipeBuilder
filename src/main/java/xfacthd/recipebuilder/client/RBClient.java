package xfacthd.recipebuilder.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.data.builders.*;
import xfacthd.recipebuilder.client.screen.BuilderScreen;
import xfacthd.recipebuilder.client.data.BuilderType;
import xfacthd.recipebuilder.common.net.PacketOpenBuilder;
import xfacthd.recipebuilder.common.util.Utils;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RecipeBuilder.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RBClient
{
    public static final Map<IRecipeSerializer<?>, BuilderType> BUILDERS = new Object2ObjectArrayMap<>();

    public static final Lazy<KeyBinding> KEY_BIND_OPEN_BUILDER = makeKeyBind();

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ClientRegistry.registerKeyBinding(KEY_BIND_OPEN_BUILDER.get());

            ScreenManager.register(RecipeBuilder.BUILDER_CONTAINER.get(), BuilderScreen::new);
        });

        MinecraftForge.EVENT_BUS.addListener(RBClient::onMainMenuOpen);
        MinecraftForge.EVENT_BUS.addListener(RBClient::onClientTickStart);

        BUILDERS.put(IRecipeSerializer.SHAPED_RECIPE, new ShapedCraftingBuilder());
        BUILDERS.put(IRecipeSerializer.SHAPELESS_RECIPE, new ShapelessCraftingBuilder());
        BUILDERS.put(IRecipeSerializer.SMELTING_RECIPE, new CookingBuilder(IRecipeSerializer.SMELTING_RECIPE, 200));
        BUILDERS.put(IRecipeSerializer.BLASTING_RECIPE, new CookingBuilder(IRecipeSerializer.BLASTING_RECIPE, 100));
        BUILDERS.put(IRecipeSerializer.SMOKING_RECIPE, new CookingBuilder(IRecipeSerializer.SMOKING_RECIPE, 100));
        BUILDERS.put(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, new CookingBuilder(IRecipeSerializer.CAMPFIRE_COOKING_RECIPE, 100, 9));
        BUILDERS.put(IRecipeSerializer.STONECUTTER, new StonecuttingBuilder());
        BUILDERS.put(IRecipeSerializer.SMITHING, new SmithingBuilder());
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
        if (event.phase != TickEvent.Phase.START) { return; }

        if (KEY_BIND_OPEN_BUILDER.get().consumeClick() && Minecraft.getInstance().screen == null)
        {
            RecipeBuilder.NETWORK.sendToServer(new PacketOpenBuilder());
        }
    }

    private static Lazy<KeyBinding> makeKeyBind()
    {
        return Lazy.of(() -> new KeyBinding("recipebuilder.key.open_builder", GLFW.GLFW_KEY_B, "key.categories.recipebuilder"));
    }
}