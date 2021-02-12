package uk.kihira.playerrugs.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.StringUtils;
import uk.kihira.playerrugs.common.tileentities.PlayerRugTE;

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
                    gameprofile = PlayerRugTE.updateGameProfile(gameprofile1);
                    compoundnbt.remove("PlayerProfile");
                    compoundnbt.put("PlayerProfile", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
                }
            }
            matrixStack.push();
            matrixStack.translate(3f/16f, 3f/16f, 0.5D);
            if(type == TransformType.GUI) {
                matrixStack.rotate(Vector3f.XN.rotationDegrees(-45));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(45));
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.translate(0.0D, 0.75D, 0.0D);
            }
            if(type == TransformType.GROUND) {
                matrixStack.translate(1f/16f,0,-0.25);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.translate(0,0.25,0);
            }
            if(type == TransformType.FIRST_PERSON_RIGHT_HAND) {
                matrixStack.rotate(Vector3f.XN.rotationDegrees(-15));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(15));
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.translate(6f/16f,1,0);
            }
            if(type == TransformType.FIRST_PERSON_LEFT_HAND) {
                matrixStack.rotate(Vector3f.XN.rotationDegrees(-15));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(-15));
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.translate(0,1,-(6f/16f));
            }
            if(type == TransformType.THIRD_PERSON_RIGHT_HAND) {
                matrixStack.scale(0.75F, 0.75F, 0.75F);
                matrixStack.rotate(Vector3f.XN.rotationDegrees(-90));
                matrixStack.translate(-(1f/16f),-(1f/16f),-(10f/16f));
            }
            if(type == TransformType.THIRD_PERSON_LEFT_HAND) {
                matrixStack.scale(0.75F, 0.75F, 0.75F);
                matrixStack.rotate(Vector3f.XN.rotationDegrees(-90));
                matrixStack.translate(-(1f/16f),-(1f/16f),-(10f/16f));
            }
            if(type == TransformType.FIXED) {
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.translate(18f/16f,18f/16f,2f/16f);
                matrixStack.rotate(Vector3f.XN.rotationDegrees(90));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
            }
            PlayerRugTESR.render(Direction.NORTH, gameprofile, matrixStack, buffer, combinedLight, false);
            matrixStack.pop();
        }
    }
}
