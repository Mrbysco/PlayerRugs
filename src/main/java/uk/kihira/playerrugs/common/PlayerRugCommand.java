package uk.kihira.playerrugs.common;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.item.ItemStack;

public class PlayerRugCommand {
    public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("playerrug");
        root.requires((sourceStack) -> sourceStack.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.word()).executes(PlayerRugCommand::giveRug));
        dispatcher.register(root);
    }

    @SuppressWarnings("SameReturnValue")
    private static int giveRug(CommandContext<CommandSourceStack> ctx) {
        final CommandSourceStack source = ctx.getSource();
        final MinecraftServer server = source.getServer();
        final String username = StringArgumentType.getString(ctx, "name");

        ServerPlayer player = source.getPlayer();
        if (player != null) {
            GameProfileCache profileCache = server.getProfileCache();
            GameProfile profile = !username.isEmpty() && profileCache != null ? profileCache.get(username).orElse(player.getGameProfile()) : player.getGameProfile();

            ItemStack rugStack = RugRegistry.PLAYER_RUG_ITEM.get().getDefaultInstance();
            CompoundTag tag = rugStack.getOrCreateTag();
            tag.putString("PlayerProfile", profile.getName());
            player.addItem(rugStack);
        }

        return 0;
    }
}
