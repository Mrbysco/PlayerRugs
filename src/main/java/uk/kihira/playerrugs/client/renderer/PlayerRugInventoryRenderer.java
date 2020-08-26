package uk.kihira.playerrugs.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class PlayerRugInventoryRenderer extends ItemStackTileEntityRenderer {
    @Override
    public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if(!stack.isEmpty()) {
            GameProfile gameprofile = null;
            if (stack.hasTag() && stack.getTag() != null) {
                CompoundNBT compoundnbt = stack.getTag();
                if (compoundnbt.contains("PlayerProfile", 10)) {
                    gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("PlayerProfile"));
                } else if (compoundnbt.contains("PlayerProfile", 8) && !StringUtils.isBlank(compoundnbt.getString("PlayerProfile"))) {
                    GameProfile gameprofile1 = new GameProfile((UUID)null, compoundnbt.getString("PlayerProfile"));
                    gameprofile = SkullTileEntity.updateGameProfile(gameprofile1);
                    compoundnbt.remove("PlayerProfile");
                    compoundnbt.put("PlayerProfile", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
                }
            }
            PlayerRugTESR.renderItem(type, Direction.NORTH, gameprofile, matrixStack, buffer, combinedLight);
        }
    }
}
