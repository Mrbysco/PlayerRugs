package uk.kihira.playerrugs.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

public class PlayerRugCommand {
	public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("playerrug");
		root.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.argument("name", StringArgumentType.word()).executes(PlayerRugCommand::giveRug));
		dispatcher.register(root);
	}

	private static int giveRug(CommandContext<CommandSourceStack> ctx) {
		final CommandSourceStack source = ctx.getSource();
		final String username = StringArgumentType.getString(ctx, "name");

		ServerPlayer player = source.getPlayer();
		if (player != null) {
			ResolvableProfile profile = ResolvableProfile.createUnresolved(username);
			ItemStack newRug = RugRegistry.PLAYER_RUG_ITEM.get().getDefaultInstance();
			newRug.set(DataComponents.PROFILE, profile);
			player.addItem(newRug);
		}

		return 0;
	}
}
