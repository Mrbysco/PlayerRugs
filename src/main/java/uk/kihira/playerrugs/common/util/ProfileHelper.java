package uk.kihira.playerrugs.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.StringUtils;
import uk.kihira.playerrugs.common.RugRegistry;

import java.util.UUID;

public class ProfileHelper {
    public static GameProfile getGameProfileFromStack(ItemStack stack) {
        if (stack == null || !stack.hasTag()) {
            return null;
        }
        GameProfile playerProfile = null;
        CompoundNBT nbttagcompound = stack.getTag() != null ? stack.getTag() : new CompoundNBT();

        if (nbttagcompound.contains("SkullOwner", 10)) {
            playerProfile = NBTUtil.readGameProfile(nbttagcompound.getCompound("SkullOwner"));
        }
        // Old version skulls
        else if (nbttagcompound.contains("SkullOwner", 8) && nbttagcompound.getString("SkullOwner").length() > 0) {
            playerProfile = new GameProfile(null, nbttagcompound.getString("SkullOwner"));
        }

        return playerProfile;
    }

    public static ItemStack getPlayerRugStack(GameProfile profile) {
        ItemStack itemStack = new ItemStack(RugRegistry.PLAYER_RUG.get());
        return addGameProfileToStack(itemStack, profile);
    }

    public static ItemStack addGameProfileToStack(ItemStack stack, GameProfile profile) {
        CompoundNBT tag = new CompoundNBT();

        if (profile != null && !StringUtils.isNullOrEmpty(profile.getName())) {
            GameProfile gameprofile = new GameProfile((UUID)null, profile.getName());
            gameprofile = SkullTileEntity.updateGameProfile(gameprofile);
            tag.put("PlayerProfile", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
        }
        stack.setTag(tag);

        return stack;
    }
}
