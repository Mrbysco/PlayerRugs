package uk.kihira.playerrugs.common.handler;


import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import uk.kihira.playerrugs.common.block.PlayerRugBlock;
import uk.kihira.playerrugs.common.config.RugConfig;
import uk.kihira.playerrugs.common.item.PlayerRugItem;
import uk.kihira.playerrugs.common.util.ProfileHelper;

public class RugEventHandler {
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypes.FALLING_ANVIL)) {
            ItemStack itemStack = ProfileHelper.getPlayerRugStack(player.getGameProfile());
            player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), itemStack));
        }
    }

    /**
     * Called to update the anvil result when easy crafting is enabled
     * @param outputStack The output stack from the anvil
     * @param player The player taking the result
     */
    public static void updateAnvilResult(ItemStack outputStack, Player player) {
        if (RugConfig.SERVER.easyCrafting.get() &&
                Block.byItem(outputStack.getItem()) instanceof PlayerRugBlock &&
                outputStack.has(DataComponents.CUSTOM_NAME) && !player.level().isClientSide()) {
            Component customName = outputStack.getCustomName();
            if (customName != null) {
                String stackName = customName.getString();
                if (stackName.isBlank() && stackName.contains(" ")) return;
                ResolvableProfile profile = ResolvableProfile.createUnresolved(stackName);
                outputStack.set(DataComponents.PROFILE, profile);
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
            resultStack.set(DataComponents.PROFILE, ProfileHelper.getGameProfileFromStack(skullStack));
        }
    }
}
