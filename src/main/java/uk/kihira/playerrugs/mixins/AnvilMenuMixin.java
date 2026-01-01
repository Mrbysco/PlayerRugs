package uk.kihira.playerrugs.mixins;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.kihira.playerrugs.common.handler.RugEventHandler;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Inject(method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "RETURN"))
    public void playerrugs$onTake(Player player, ItemStack resultStack, CallbackInfo ci) {
        RugEventHandler.updateAnvilResult(resultStack, player);
    }
}
