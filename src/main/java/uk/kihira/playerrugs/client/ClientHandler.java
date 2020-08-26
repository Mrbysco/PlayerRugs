package uk.kihira.playerrugs.client;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.kihira.playerrugs.client.renderer.PlayerRugTESR;
import uk.kihira.playerrugs.common.RugRegistry;

public class ClientHandler {
    public static void registerRenders(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(RugRegistry.PLAYER_RUG_TILE.get(), PlayerRugTESR::new);
    }
}
