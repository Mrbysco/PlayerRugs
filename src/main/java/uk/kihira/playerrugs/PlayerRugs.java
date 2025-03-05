package uk.kihira.playerrugs;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
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

    public PlayerRugs(IEventBus eventBus, Dist dist, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, RugConfig.serverSpec);
        eventBus.register(RugConfig.class);

        RugRegistry.BLOCKS.register(eventBus);
        RugRegistry.ITEMS.register(eventBus);
        RugRegistry.BLOCK_ENTITIES.register(eventBus);

        NeoForge.EVENT_BUS.addListener(this::onCommandRegister);
        NeoForge.EVENT_BUS.addListener(this::serverAboutToStart);
        eventBus.addListener(this::addTabContents);

        NeoForge.EVENT_BUS.register(new RugEventHandler());

        if (dist.isClient()) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            eventBus.addListener(ClientHandler::registerRenders);
            NeoForge.EVENT_BUS.addListener(ClientHandler::onLogin);
            NeoForge.EVENT_BUS.addListener(ClientHandler::onRespawn);
            NeoForge.EVENT_BUS.addListener(ClientHandler::onUnload);
        }

    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        PlayerRugCommand.initializeCommands(event.getDispatcher());
    }

    public void serverAboutToStart(final ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        PlayerRugBlockEntity.setup(server.services, server);
        GameProfileCache.setUsesAuthentication(server.usesAuthentication());
    }

    private void addTabContents(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(RugRegistry.PLAYER_RUG_ITEM);
        }
    }
}
