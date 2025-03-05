package uk.kihira.playerrugs.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;
import uk.kihira.playerrugs.common.RugRegistry;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;


public class ProfileHelper {
    @Nullable
    public static ResolvableProfile getGameProfileFromStack(ItemStack stack) {
        if (stack == null || !stack.has(DataComponents.PROFILE)) {
            return null;
        }
        return stack.get(DataComponents.PROFILE);
    }

    public static ItemStack getPlayerRugStack(@Nullable GameProfile profile) {
        if (profile == null) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = RugRegistry.PLAYER_RUG_ITEM.get().getDefaultInstance();
        return addGameProfileToStack(itemStack, profile);
    }

    public static ItemStack addGameProfileToStack(ItemStack stack, @Nullable GameProfile profile) {
        if (profile == null) {
            return stack;
        }

        ResolvableProfile resolvableProfile = new ResolvableProfile(profile);
        stack.set(DataComponents.PROFILE, resolvableProfile);

        if (!resolvableProfile.isResolved()) {
            PlayerRugBlockEntity.resolve(resolvableProfile).thenAcceptAsync(finalProfile -> {
                stack.set(DataComponents.PROFILE, finalProfile);
            }, PlayerRugBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
        }

        return stack;
    }
}
