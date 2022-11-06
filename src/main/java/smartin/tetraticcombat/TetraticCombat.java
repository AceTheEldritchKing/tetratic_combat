package smartin.tetraticcombat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smartin.tetraticcombat.ItemResolver.ReloadListener;
import smartin.tetraticcombat.ItemResolver.Resolver;
import smartin.tetraticcombat.network.ClientProxy;
import smartin.tetraticcombat.network.CommonProxy;
import smartin.tetraticcombat.network.PacketHandler;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("tetraticcombat")
public class TetraticCombat {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static String MODID = "tetraticcombat";

    public static CommonProxy PROXY;

    public static ReloadListener ReloadEvent;

    public TetraticCombat() {
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfigHolder.COMMON_SPEC);
    }

    @SubscribeEvent
    public void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getPlayer() instanceof ServerPlayer) {
            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), Resolver.configFileToSSyncConfig());
        }
    }

    @SubscribeEvent
    public void addReloadListener(AddReloadListenerEvent event) {
        ReloadEvent = new ReloadListener();
        event.addListener(ReloadEvent);
    }
}
