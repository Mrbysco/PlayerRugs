package uk.kihira.playerrugs.common.handler;


import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.playerrugs.common.block.PlayerRugBlock;
import uk.kihira.playerrugs.common.config.RugConfig;
import uk.kihira.playerrugs.common.item.PlayerRugItem;
import uk.kihira.playerrugs.common.util.ProfileHelper;

import java.util.Optional;

public class RugEventHandler {
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypes.FALLING_ANVIL)) {
            ItemStack itemStack = ProfileHelper.getPlayerRugStack(player.getGameProfile());
            player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), itemStack));
        }
    }

    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack stack = event.getOutput();
        Player player = event.getEntity();
        if (RugConfig.SERVER.easyCrafting.get() &&
                Block.byItem(stack.getItem()) instanceof PlayerRugBlock &&
                stack.hasCustomHoverName() && !player.level().isClientSide) {
            final MinecraftServer server = player.getServer();
            String stackName = stack.getDisplayName().getString();
            if (server != null && !stackName.contains(" ") && player.getServer() != null) {
                GameProfileCache profileCache = server.getProfileCache();
                Optional<GameProfile> profile = !stackName.isEmpty() && profileCache != null ? profileCache.get(stackName) : Optional.of(player.getGameProfile());
                ProfileHelper.addGameProfileToStack(stack, profile.orElse(null));
            }
        }
    }

    @SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event) {
        Container inventory = event.getInventory();
        ItemStack skullStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack foundStack = inventory.getItem(i);
            if (!foundStack.isEmpty() && foundStack.getItem() == Items.PLAYER_HEAD) {
                skullStack = foundStack;
            }
        }
        ItemStack resultStack = event.getCrafting();
        if (!skullStack.isEmpty() || skullStack.getItem() == Items.PLAYER_HEAD && resultStack.getItem() instanceof PlayerRugItem) {
            ProfileHelper.addGameProfileToStack(resultStack, ProfileHelper.getGameProfileFromStack(skullStack));
        }
    }
}
