package uk.kihira.playerrugs.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import uk.kihira.playerrugs.PlayerRugs;

public class RugConfig {
    public static class Server {
        public final BooleanValue easyCrafting;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server settings")
                    .push("Server");

            easyCrafting = builder
                    .comment("If true, allows rugs to be renamed in anvils to get that players rug")
                    .define("easyCrafting", false);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec serverSpec;
    public static final RugConfig.Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(RugConfig.Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        PlayerRugs.LOGGER.debug("Loaded Player Rugs' config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
        PlayerRugs.LOGGER.debug("Player Rugs' config just got changed on the file system!");
    }
}
