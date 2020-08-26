package uk.kihira.playerrugs.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import uk.kihira.playerrugs.common.util.ProfileHelper;

public class PlayerRugCommand {
    public static void initializeCommands (CommandDispatcher<CommandSource> dispatcher) {
        final LiteralArgumentBuilder<CommandSource> root = Commands.literal("playerrug");
        root.requires((p_198721_0_) -> p_198721_0_.hasPermissionLevel(3))
                .then(Commands.argument("name", StringArgumentType.word()).executes(PlayerRugCommand::giveRug));
        dispatcher.register(root);
    }

    private static int giveRug(CommandContext<CommandSource> ctx) {
        final String username = StringArgumentType.getString(ctx, "name");

        Entity sender = ctx.getSource().getEntity();
        if(sender instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)ctx.getSource().getEntity();
            ItemStack itemStack = ProfileHelper.getPlayerRugStack(!username.isEmpty() && player.getServer() != null ? player.getServer().getPlayerProfileCache().getGameProfileForUsername(username) : player.getGameProfile());
            player.addItemStackToInventory(itemStack);
        }

        return 0;
    }
}
