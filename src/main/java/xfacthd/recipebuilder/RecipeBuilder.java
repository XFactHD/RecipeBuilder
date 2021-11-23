package xfacthd.recipebuilder;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xfacthd.recipebuilder.common.container.BuilderContainer;
import xfacthd.recipebuilder.common.net.PacketOpenBuilder;

@Mod(RecipeBuilder.MOD_ID)
public class RecipeBuilder
{
    public static final String MOD_ID = "recipebuilder";
    public static final Logger LOGGER = LogManager.getLogger();

    private static final String NET_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> NET_VERSION,
            NET_VERSION::equals,
            NET_VERSION::equals
    );

    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);
    public static final RegistryObject<ContainerType<BuilderContainer>> BUILDER_CONTAINER = CONTAINERS.register("builder", () -> new ContainerType<>(BuilderContainer::new));

    public RecipeBuilder()
	{
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        initPackets();
    }

    @SuppressWarnings("UnusedAssignment")
    private void initPackets()
    {
        int packet = 0;

        NETWORK.messageBuilder(PacketOpenBuilder.class, packet++)
                .encoder((pkt, buf) -> {})
                .decoder(buf -> new PacketOpenBuilder())
                .consumer(PacketOpenBuilder::handle)
                .add();
    }
}
