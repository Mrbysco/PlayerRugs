package uk.kihira.playerrugs.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import uk.kihira.playerrugs.common.block.PlayerRugBlock;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

import javax.annotation.Nullable;
import java.util.Map;

public class PlayerRugBER implements BlockEntityRenderer<PlayerRugBlockEntity> {
    public static final ResourceLocation defaultTexture = DefaultPlayerSkin.getDefaultSkin();

    public final SkullModel headModel;

    public PlayerRugBER(BlockEntityRendererProvider.Context context) {
        this.headModel = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    public void render(PlayerRugBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState blockstate = blockEntity.getBlockState();
        boolean flag = blockstate.getBlock() instanceof PlayerRugBlock;
        Direction direction = flag ? blockstate.getValue(PlayerRugBlock.FACING) : Direction.UP;
        GameProfile profile = blockEntity.getPlayerProfile();
        boolean standing = blockstate.getValue(PlayerRugBlock.STANDING);

        render(direction, profile, blockEntity.isSlim(), standing, poseStack, buffer, packedLight);
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

        RenderType headType = getRenderType(profile);
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

        RenderType rugType = PRRenderType.playerRug(getSkinLocation(profile));
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
            buildBodyPart(builder, poseStack,
                    xOffset + (slimModel ? 1f / 16f : 0f), yOffset, zOffset,
                    (slimModel ? 3f : 4f) / 16f, thickness, 12f / 16f,
                    (slimModel ? 39f : 40f) / texWidth, 52f / texHeight, 36f / texWidth, 64f / texHeight,
                    texWidth, texHeight, combinedLight);
        } else {
            buildBodyPart(builder, poseStack,
                    xOffset, yOffset, zOffset - (slimModel ? 1f / 16f : 0f),
                    -12f / 16f, thickness, -(slimModel ? 3f : 4f) / 16f,
                    (slimModel ? 46f : 48f) / texWidth, 52f / texHeight, (slimModel ? 43f : 44f) / texWidth, 64f / texHeight,
                    texWidth, texHeight, combinedLight);
        }

        // Right Arm
        xOffset = 12f / 16f - 0.5f;
        zOffset = 1f / 16f - 0.5f;
        if (standing) {
            buildBodyPart(builder, poseStack,
                    xOffset, yOffset, zOffset,
                    (slimModel ? 3f : 4f) / 16f, thickness, 12f / 16f,
                    (slimModel ? 47f : 48f) / texWidth, 20f / texHeight, 44f / texWidth, 32f / texHeight,
                    texWidth, texHeight, combinedLight);
        } else {
            buildBodyPart(builder, poseStack,
                    xOffset, yOffset, zOffset,
                    12f / 16f, thickness, (slimModel ? 3f : 4f) / 16f,
                    (slimModel ? 54f : 56f) / texWidth, 20f / texHeight, (slimModel ? 51f : 52f) / texWidth, 32f / texHeight,
                    texWidth, texHeight, combinedLight);
        }

        // Body
        xOffset = 0.25f - 0.5f;
        zOffset = 1f / 16f - 0.5f;
        buildBodyPart(builder, poseStack,
                xOffset, yOffset, zOffset,
                8f / 16f, thickness, 12f / 16f,
                (standing ? 28f : 32f) / texWidth, 20f / texHeight, (standing ? 20f : 40f) / texWidth, 32f / texHeight,
                texWidth, texHeight, combinedLight);

        // Left Leg
        xOffset = 0.25f - 0.5f;
        zOffset = 13f / 16f - 0.5f;
        buildBodyPart(builder, poseStack,
                xOffset, yOffset, zOffset,
                4f / 16f, thickness, 12f / 16f,
                (standing ? 20f : 28f) / texWidth, 52f / texHeight, (standing ? 24f : 32f) / texWidth, 64f / texHeight,
                texWidth, texHeight, combinedLight);

        // Right Leg
        xOffset = 0.0f;
        zOffset = 13f / 16f - 0.5f;
        buildBodyPart(builder, poseStack,
                xOffset, yOffset, zOffset,
                4f / 16f, thickness, 12f / 16f,
                (standing ? 4f : 12f) / texWidth, 20f / texHeight, (standing ? 8f : 16f) / texWidth, 32f / texHeight,
                texWidth, texHeight, combinedLight);

        if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
            bufferSource1.endBatch(rugType);
        }

        poseStack.popPose();
    }

    public static RenderType getRenderType(@Nullable GameProfile gameProfileIn) {
        if (gameProfileIn == null || !gameProfileIn.isComplete()) {
            return RenderType.entityCutoutNoCull(defaultTexture);
        } else {
            final Minecraft minecraft = Minecraft.getInstance();
            final Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gameProfileIn);
            if (map.containsKey(Type.SKIN)) {
                return RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN));
            } else {
                return RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(gameProfileIn)));
            }
        }
    }

    public static ResourceLocation getSkinLocation(@Nullable GameProfile gameProfileIn) {
        if (gameProfileIn == null || !gameProfileIn.isComplete()) {
            return defaultTexture;
        } else {
            final Minecraft minecraft = Minecraft.getInstance();
            final Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gameProfileIn);
            if (map.containsKey(Type.SKIN)) {
                return minecraft.getSkinManager().registerTexture((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN);
            } else {
                return DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(gameProfileIn));
            }
        }
    }

    public static void buildBodyPart(VertexConsumer builder, PoseStack poseStack, float xPos, float yPos, float zPos, float width, float depth, float length, float minU, float minV, float maxU, float maxV, float texWidth, float texHeight, int combinedLight) {
        PoseStack.Pose pose = poseStack.last();
        Vec3i downVec = Direction.DOWN.getNormal();
        Vec3i upVec = Direction.UP.getNormal();
        Vec3i northVec = Direction.NORTH.getNormal();
        Vec3i southVec = Direction.SOUTH.getNormal();
        Vec3i westVec = Direction.WEST.getNormal();
        Vec3i eastVec = Direction.EAST.getNormal();

        float texDepth = depth * 16f;
        // This if is used if texture should be rotated as width would be longer then length (used for arms)
        if (Math.abs(width) > Math.abs(length)) {
            // Draws base texture
            addVertexWithUV(builder, pose, xPos, yPos, zPos, minU, minV, combinedLight, upVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos + length, maxU, minV, combinedLight, upVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos + length, maxU, maxV, combinedLight, upVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos, minU, maxV, combinedLight, upVec);

            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos, minU, maxV, combinedLight, downVec);
            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos + length, maxU, maxV, combinedLight, downVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos + length, maxU, minV, combinedLight, downVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos, minU, minV, combinedLight, downVec);

            // Draws sides
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos, minU, minV + (texDepth / texHeight), combinedLight, northVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos + length, maxU, minV + (texDepth / texHeight), combinedLight, northVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos + length, maxU, minV, combinedLight, northVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos, minU, minV, combinedLight, northVec);

            float uUpper = maxU + ((minU > maxU) ? (texDepth / texWidth) : -(texDepth / texWidth));
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos + length, uUpper, minV, combinedLight, eastVec);
            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos + length, uUpper, maxV, combinedLight, eastVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos + length, maxU, maxV, combinedLight, eastVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos + length, maxU, minV, combinedLight, eastVec);

            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos + length, maxU, maxV - (texDepth / texHeight), combinedLight, southVec);
            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos, minU, maxV - (texDepth / texHeight), combinedLight, southVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos, minU, maxV, combinedLight, southVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos + length, maxU, maxV, combinedLight, southVec);

            uUpper = minU + ((minU < maxU) ? (texDepth / texWidth) : -(texDepth / texWidth));
            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos, minU, maxV, combinedLight, westVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos, minU, minV, combinedLight, westVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos, uUpper, minV, combinedLight, westVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos, uUpper, maxV, combinedLight, westVec);
        } else {
            // Draws base texture
            addVertexWithUV(builder, pose, xPos, yPos, zPos, minU, minV, combinedLight, upVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos + length, minU, maxV, combinedLight, upVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos + length, maxU, maxV, combinedLight, upVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos, maxU, minV, combinedLight, upVec);

            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos, maxU, minV, combinedLight, downVec);
            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos + length, maxU, maxV, combinedLight, downVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos + length, minU, maxV, combinedLight, downVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos, minU, minV, combinedLight, downVec);

            // Draws sides
            float uUpper = minU + ((minU < maxU) ? (texDepth / texWidth) : -(texDepth / texWidth));
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos, minU, minV, combinedLight, northVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos + length, minU, maxV, combinedLight, northVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos + length, uUpper, maxV, combinedLight, northVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos, uUpper, minV, combinedLight, northVec);

            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos + length, minU, maxV, combinedLight, eastVec);
            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos + length, maxU, maxV, combinedLight, eastVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos + length, maxU, maxV - (texDepth / texHeight), combinedLight, eastVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos + length, minU, maxV - (texDepth / texHeight), combinedLight, eastVec);

            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos + length, maxU, maxV, combinedLight, southVec);
            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos, maxU, minV, combinedLight, southVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos, maxU - (texDepth / texWidth), minV, combinedLight, southVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos + length, maxU - (texDepth / texWidth), maxV, combinedLight, southVec);

            addVertexWithUV(builder, pose, xPos + width, yPos - depth, zPos, maxU, minV, combinedLight, westVec);
            addVertexWithUV(builder, pose, xPos, yPos - depth, zPos, minU, minV, combinedLight, westVec);
            addVertexWithUV(builder, pose, xPos, yPos, zPos, minU, minV + (texDepth / texHeight), combinedLight, westVec);
            addVertexWithUV(builder, pose, xPos + width, yPos, zPos, maxU, minV + (texDepth / texHeight), combinedLight, westVec);
        }
    }

    private static void addVertexWithUV(VertexConsumer builder, PoseStack.Pose pose,
                                        float x, float y, float z, float u, float v,
                                        int combinedLight, Vec3i directionVec) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        builder.vertex(matrix4f, x, y, z)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(matrix3f, directionVec.getX(), directionVec.getY(), directionVec.getZ())
                .endVertex();
    }
}
