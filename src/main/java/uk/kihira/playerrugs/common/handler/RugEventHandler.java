package uk.kihira.playerrugs.common.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.common.blocks.PlayerRugBlock;
import uk.kihira.playerrugs.common.config.RugConfig;
import uk.kihira.playerrugs.common.items.PlayerRugItem;
import uk.kihira.playerrugs.common.util.ProfileHelper;

public class RugEventHandler {
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity && event.getSource() == DamageSource.ANVIL) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack itemStack = ProfileHelper.getPlayerRugStack(player.getGameProfile());
            player.world.addEntity(new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), itemStack));
        }
    }

    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack stack = event.getItemResult();
        if (RugConfig.SERVER.easyCrafting.get() && Block.getBlockFromItem(stack.getItem()) instanceof PlayerRugBlock && stack.hasDisplayName() && !event.getPlayer().world.isRemote) {
            String stackName = stack.getDisplayName().getUnformattedComponentText();
            PlayerEntity player = event.getPlayer();
            if(!stackName.contains(" ") && player.getServer() != null) {
                ProfileHelper.addGameProfileToStack(stack, !stackName.isEmpty() ? player.getServer().getPlayerProfileCache().getGameProfileForUsername(stackName) : player.getGameProfile());
            }
        }
    }

    @SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event) {
        IInventory inventory = event.getInventory();
        ItemStack skullStack = ItemStack.EMPTY;
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack foundStack = inventory.getStackInSlot(i);
            if(!foundStack.isEmpty() && foundStack.getItem() == Items.PLAYER_HEAD) {
                skullStack = foundStack;
            }
        }
        ItemStack resultStack = event.getCrafting();
        if (!skullStack.isEmpty() || skullStack.getItem() == Items.PLAYER_HEAD && resultStack.getItem() instanceof PlayerRugItem) {
            ProfileHelper.addGameProfileToStack(resultStack, ProfileHelper.getGameProfileFromStack(skullStack));
        }
    }
}
