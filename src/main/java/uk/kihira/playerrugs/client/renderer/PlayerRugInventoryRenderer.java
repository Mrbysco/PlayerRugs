package uk.kihira.playerrugs.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import uk.kihira.playerrugs.PlayerRugs;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerRugInventoryRenderer extends BlockEntityWithoutLevelRenderer {
    public final SkullModel headModel;

    public PlayerRugInventoryRenderer(BlockEntityRendererProvider.Context context) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.headModel = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!stack.isEmpty()) {
            GameProfile gameprofile = null;
            if (stack.hasTag() && stack.getTag() != null) {
                CompoundTag compoundtag = stack.getTag();
                if (compoundtag.contains("PlayerProfile", 10)) {
                    gameprofile = NbtUtils.readGameProfile(compoundtag.getCompound("PlayerProfile"));
                } else if (compoundtag.contains("PlayerProfile", 8) && !Util.isBlank(compoundtag.getString("PlayerProfile"))) {
                    GameProfile gameprofile1 = new GameProfile((UUID) null, compoundtag.getString("PlayerProfile"));
                    compoundtag.remove("PlayerProfile");
                    PlayerRugBlockEntity.updateGameProfile(gameprofile1, (profile) -> {
                        compoundtag.put("PlayerProfile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
                    });
                }
            }
            poseStack.pushPose();
            poseStack.translate(3f / 16f, 3f / 16f, 0.5D);
            if (type == ItemDisplayContext.GUI) {
                poseStack.mulPose(Axis.XN.rotationDegrees(-45));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0D, 0.75D, 0.0D);
            }
            if (type == ItemDisplayContext.GROUND) {
                poseStack.translate(1f / 16f, 0, -0.25);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0, 0.25, 0);
            }
            if (type == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                poseStack.mulPose(Axis.XN.rotationDegrees(-15));
                poseStack.mulPose(Axis.YP.rotationDegrees(15));
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(6f / 16f, 1, 0);
            }
            if (type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                poseStack.mulPose(Axis.XN.rotationDegrees(-15));
                poseStack.mulPose(Axis.YP.rotationDegrees(-15));
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0, 1, -(6f / 16f));
            }
            if (type == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                poseStack.scale(0.75F, 0.75F, 0.75F);
                poseStack.mulPose(Axis.XN.rotationDegrees(-90));
                poseStack.translate(-(1f / 16f), -(1f / 16f), -(10f / 16f));
            }
            if (type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                poseStack.scale(0.75F, 0.75F, 0.75F);
                poseStack.mulPose(Axis.XN.rotationDegrees(-90));
                poseStack.translate(-(1f / 16f), -(1f / 16f), -(10f / 16f));
            }
            if (type == ItemDisplayContext.FIXED) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(18f / 16f, 18f / 16f, 2f / 16f);
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            //Render the inventory model as slim (until we figure out how to get the model type from the gameprofile)
            render(Direction.NORTH, gameprofile, true, false, poseStack, buffer, packedLight);
            poseStack.popPose();
        }
    }

    public void render(Direction direction, @Nullable GameProfile profile, boolean slimModel, boolean standing,
                       PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
        poseStack.translate(0.5f, 0.001d, 0.5f);
        // Render head
        poseStack.pushPose();
//		RenderSystem.enableRescaleNormal();

        poseStack.translate(0, (standing ? 0.4999f : 0f), 0);

        float angle = (direction.get2DDataValue() + 2) * -90f;

        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0, -0.001, standing ? 8f / 16f : -9f / 16f);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        RenderType headType = PlayerRugBER.getRenderType(profile);
        VertexConsumer buffer = bufferSource.getBuffer(headType);
        headModel.renderToBuffer(poseStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

//		RenderSystem.disableRescaleNormal();
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));

        if (standing) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.translate(0f, 7f / 16f, -1f / 16f);
        }

        if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
            bufferSource1.endBatch(headType);
        }

        RenderType rugType = PRRenderType.playerRug(PlayerRugBER.getSkinLocation(profile));
        VertexConsumer builder = bufferSource.getBuffer(rugType);

        float texHeight = 64;
        float texWidth = 64;
        float xOffset = 4f / 16f - 0.5f;
        float zOffset = 5f / 16f - 0.5f;
        float thickness = 1f / 16f;
        float yOffset = 1f / 16f;

        // Left Arm
        if (standing) {
            xOffset = -0.5f;
            zOffset = 1f / 16f - 0.5f;
            PlayerRugBER.buildBodyPart(builder, poseStack,
                    xOffset + (slimModel ? 1f / 16f : 0f), yOffset, zOffset,
                    (slimModel ? 3f : 4f) / 16f, thickness, 12f / 16f,
                    (slimModel ? 39f : 40f) / texWidth, 52f / texHeight, 36f / texWidth, 64f / texHeight,
                    texWidth, texHeight, combinedLight);
        } else {
            PlayerRugBER.buildBodyPart(builder, poseStack,
                    xOffset, yOffset, zOffset - (slimModel ? 1f / 16f : 0f),
                    -12f / 16f, thickness, -(slimModel ? 3f : 4f) / 16f,
                    (slimModel ? 46f : 48f) / texWidth, 52f / texHeight, (slimModel ? 43f : 44f) / texWidth, 64f / texHeight,
                    texWidth, texHeight, combinedLight);
        }

        // Right Arm
        xOffset = 12f / 16f - 0.5f;
        zOffset = 1f / 16f - 0.5f;
        if (standing) {
            PlayerRugBER.buildBodyPart(builder, poseStack,
                    xOffset, yOffset, zOffset,
                    (slimModel ? 3f : 4f) / 16f, thickness, 12f / 16f,
                    (slimModel ? 47f : 48f) / texWidth, 20f / texHeight, 44f / texWidth, 32f / texHeight,
                    texWidth, texHeight, combinedLight);
        } else {
            PlayerRugBER.buildBodyPart(builder, poseStack,
                    xOffset, yOffset, zOffset,
                    12f / 16f, thickness, (slimModel ? 3f : 4f) / 16f,
                    (slimModel ? 54f : 56f) / texWidth, 20f / texHeight, (slimModel ? 51f : 52f) / texWidth, 32f / texHeight,
                    texWidth, texHeight, combinedLight);
        }

        // Body
        xOffset = 0.25f - 0.5f;
        zOffset = 1f / 16f - 0.5f;
        PlayerRugBER.buildBodyPart(builder, poseStack,
                xOffset, yOffset, zOffset,
                8f / 16f, thickness, 12f / 16f,
                (standing ? 28f : 32f) / texWidth, 20f / texHeight, (standing ? 20f : 40f) / texWidth, 32f / texHeight,
                texWidth, texHeight, combinedLight);

        // Left Leg
        xOffset = 0.25f - 0.5f;
        zOffset = 13f / 16f - 0.5f;
        PlayerRugBER.buildBodyPart(builder, poseStack,
                xOffset, yOffset, zOffset,
                4f / 16f, thickness, 12f / 16f,
                (standing ? 20f : 28f) / texWidth, 52f / texHeight, (standing ? 24f : 32f) / texWidth, 64f / texHeight,
                texWidth, texHeight, combinedLight);

        // Right Leg
        xOffset = 0.0f;
        zOffset = 13f / 16f - 0.5f;
        PlayerRugBER.buildBodyPart(builder, poseStack,
                xOffset, yOffset, zOffset,
                4f / 16f, thickness, 12f / 16f,
                (standing ? 4f : 12f) / texWidth, 20f / texHeight, (standing ? 8f : 16f) / texWidth, 32f / texHeight,
                texWidth, texHeight, combinedLight);

        if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
            bufferSource1.endBatch(rugType);
        }

        poseStack.popPose();
    }
}