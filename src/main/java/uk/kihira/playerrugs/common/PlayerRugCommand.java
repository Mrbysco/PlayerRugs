package uk.kihira.playerrugs.common;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.item.ItemStack;
import uk.kihira.playerrugs.common.util.ProfileHelper;

import java.util.Optional;

public class PlayerRugCommand {
    public static void initializeCommands (CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("playerrug");
        root.requires((sourceStack) -> sourceStack.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.word()).executes(PlayerRugCommand::giveRug));
        dispatcher.register(root);
    }

    private static int giveRug(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		final CommandSourceStack source = ctx.getSource();
	    final MinecraftServer server = source.getServer();
        final String username = StringArgumentType.getString(ctx, "name");

	    ServerPlayer player = source.getPlayer();
        if (player != null) {
	        GameProfileCache profileCache = server.getProfileCache();
			Optional<GameProfile> profile = !username.isEmpty() && profileCache != null ? profileCache.get(username) : Optional.of(player.getGameProfile());
			ItemStack itemStack = ProfileHelper.getPlayerRugStack(profile.orElse(null));

	        player.addItem(itemStack);
        }

        return 0;
    }
}
