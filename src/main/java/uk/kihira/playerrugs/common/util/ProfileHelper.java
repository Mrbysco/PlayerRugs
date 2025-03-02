package uk.kihira.playerrugs.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import uk.kihira.playerrugs.common.RugRegistry;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

import javax.annotation.Nullable;

public class ProfileHelper {
    public static GameProfile getGameProfileFromStack(ItemStack stack) {
        if (stack == null || !stack.hasTag()) {
            return null;
        }
        GameProfile playerProfile = null;
        CompoundTag tag = stack.getTag() != null ? stack.getTag() : new CompoundTag();

        if (tag.contains("SkullOwner", 10)) {
            playerProfile = NbtUtils.readGameProfile(tag.getCompound("SkullOwner"));
        }
        // Old version skulls
        else if (tag.contains("SkullOwner", 8) && !Util.isBlank(tag.getString("SkullOwner"))) {
            playerProfile = new GameProfile(null, tag.getString("SkullOwner"));
        }

        return playerProfile;
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

        if (!Util.isBlank(profile.getName())) {
            CompoundTag tag = new CompoundTag();
            tag.put("PlayerProfile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
            stack.setTag(tag);

            PlayerRugBlockEntity.updateGameProfile(profile, (newProfile) -> {
                tag.put("PlayerProfile", NbtUtils.writeGameProfile(new CompoundTag(), newProfile));
                stack.setTag(tag);
            });
        }

        return stack;
    }
}
