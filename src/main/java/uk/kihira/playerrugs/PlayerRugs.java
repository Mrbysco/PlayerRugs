package uk.kihira.playerrugs;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.kihira.playerrugs.client.ClientHandler;
import uk.kihira.playerrugs.common.PlayerRugCommand;
import uk.kihira.playerrugs.common.RugRegistry;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;
import uk.kihira.playerrugs.common.config.RugConfig;
import uk.kihira.playerrugs.common.handler.RugEventHandler;

@Mod(PlayerRugs.MOD_ID)
public class PlayerRugs {
    public static final String MOD_ID = "playerrugs";

    public static final Logger LOGGER = LogManager.getLogger();

    public PlayerRugs() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RugConfig.serverSpec);
        eventBus.register(RugConfig.class);

        RugRegistry.BLOCKS.register(eventBus);
        RugRegistry.ITEMS.register(eventBus);
        RugRegistry.BLOCK_ENTITIES.register(eventBus);

        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
        eventBus.addListener(this::addTabContents);

        MinecraftForge.EVENT_BUS.register(new RugEventHandler());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::registerRenders);
            MinecraftForge.EVENT_BUS.addListener(ClientHandler::onLogin);
            MinecraftForge.EVENT_BUS.addListener(ClientHandler::onRespawn);
            MinecraftForge.EVENT_BUS.addListener(ClientHandler::onUnload);
        });

    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        PlayerRugCommand.initializeCommands(event.getDispatcher());
    }

    public void serverAboutToStart(final ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        PlayerRugBlockEntity.setup(server.getProfileCache(), server.getSessionService(), server);
        GameProfileCache.setUsesAuthentication(server.usesAuthentication());
    }

    private void addTabContents(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(RugRegistry.PLAYER_RUG_ITEM);
        }
    }
}
