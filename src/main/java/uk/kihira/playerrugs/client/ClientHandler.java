package uk.kihira.playerrugs.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.client.renderer.PlayerRugBER;
import uk.kihira.playerrugs.client.renderer.PlayerRugInventoryRenderer;
import uk.kihira.playerrugs.common.RugRegistry;

@EventBusSubscriber
public class ClientHandler {
    @SubscribeEvent
    public static void registerRenders(RegisterRenderers event) {
        event.registerBlockEntityRenderer(RugRegistry.PLAYER_RUG_BLOCK_ENTITY.get(), PlayerRugBER::new);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(PlayerRugs.modLoc("player_rug"), PlayerRugInventoryRenderer.Unbaked.MAP_CODEC);
    }
}
