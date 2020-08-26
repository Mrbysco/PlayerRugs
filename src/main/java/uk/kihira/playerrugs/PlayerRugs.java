package uk.kihira.playerrugs;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.kihira.playerrugs.client.ClientHandler;
import uk.kihira.playerrugs.common.PlayerRugCommand;
import uk.kihira.playerrugs.common.RugRegistry;
import uk.kihira.playerrugs.common.config.RugConfig;
import uk.kihira.playerrugs.common.handler.RugEventHandler;
import uk.kihira.playerrugs.common.tileentities.PlayerRugTE;

@Mod(PlayerRugs.MOD_ID)
public class PlayerRugs {
    public static final String MOD_ID = "playerrugs";

    public static final Logger LOGGER = LogManager.getLogger();

    public PlayerRugs() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RugConfig.serverSpec);
        eventBus.register(RugConfig.class);

        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);

        RugRegistry.BLOCKS.register(eventBus);
        RugRegistry.ITEMS.register(eventBus);
        RugRegistry.TILES.register(eventBus);

        MinecraftForge.EVENT_BUS.register(new RugEventHandler());

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::registerRenders);
        });
    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        PlayerRugCommand.initializeCommands(event.getDispatcher());
    }

    public void serverAboutToStart(final FMLServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        PlayerRugTE.setProfileCache(server.getPlayerProfileCache());
        PlayerRugTE.setSessionService(server.getMinecraftSessionService());
        PlayerProfileCache.setOnlineMode(server.isServerInOnlineMode());
    }
}
