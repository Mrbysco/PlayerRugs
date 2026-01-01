package uk.kihira.playerrugs.common.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;
import uk.kihira.playerrugs.PlayerRugs;

public class RugConfig {
    public static class Server {
        public final BooleanValue easyCrafting;

        Server(ModConfigSpec.Builder builder) {
            builder.comment("Server settings")
                    .push("server");

            easyCrafting = builder
                    .comment("If true, allows rugs to be renamed in anvils to get that players rug")
                    .define("easyCrafting", false);

            builder.pop();
        }
    }

    public static final ModConfigSpec serverSpec;
    public static final RugConfig.Server SERVER;

    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(RugConfig.Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final net.neoforged.fml.event.config.ModConfigEvent.Loading configEvent) {
        PlayerRugs.LOGGER.debug("Loaded Player Rugs' config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        PlayerRugs.LOGGER.debug("Player Rugs' config just got changed on the file system!");
    }
}
