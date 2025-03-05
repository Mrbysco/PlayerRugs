package uk.kihira.playerrugs.client;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.event.level.LevelEvent;
import uk.kihira.playerrugs.client.renderer.PlayerRugBER;
import uk.kihira.playerrugs.common.RugRegistry;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

public class ClientHandler {
    public static void registerRenders(RegisterRenderers event) {
        event.registerBlockEntityRenderer(RugRegistry.PLAYER_RUG_BLOCK_ENTITY.get(), PlayerRugBER::new);
    }

    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isLocalServer()) {
            setPlayerCache(mc);
        }
    }

    public static void onRespawn(ClientPlayerNetworkEvent.Clone event) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isLocalServer()) {
            setPlayerCache(mc);
        }
    }

    private static void setPlayerCache(Minecraft mc) {
        YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(mc.getProxy());
        Services services = Services.create(authenticationService, mc.gameDirectory);
        services.profileCache().setExecutor(mc);
        PlayerRugBlockEntity.setup(services, mc);
        GameProfileCache.setUsesAuthentication(false);
    }

    public static void onUnload(LevelEvent.Unload event) {
        PlayerRugBlockEntity.clear();
    }
}
