package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;
import uk.kihira.playerrugs.common.block.PlayerRugBlock;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

import java.util.function.Supplier;

public class PlayerRugBER implements BlockEntityRenderer<PlayerRugBlockEntity, PlayerRugRenderState> {
    private final PlayerSkinRenderCache playerSkinRenderCache;
    public static final Identifier defaultTexture = DefaultPlayerSkin.getDefaultTexture();

    public final SkullModel headModel;

    public PlayerRugBER(BlockEntityRendererProvider.Context context) {
        this.headModel = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
        this.playerSkinRenderCache = context.playerSkinRenderCache();
    }

    @Override
    public PlayerRugRenderState createRenderState() {
        return new PlayerRugRenderState();
    }

    @Override
    public void extractRenderState(PlayerRugBlockEntity blockEntity, PlayerRugRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.profile = blockEntity.getPlayerProfile();

        if (renderState.profile != null) {
            SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
            Supplier<PlayerSkin> skinSupplier = skinmanager.createLookup(renderState.profile.partialProfile(), false);
            renderState.skin = playerSkinRenderCache.getOrDefault(renderState.profile).playerSkin();
            renderState.isSlim = skinSupplier.get().model().getSerializedName().equals("slim");
            renderState.renderType = getRenderType(renderState.profile);
            renderState.standing = blockEntity.isStanding();
        }
    }

    @Override
    public void submit(PlayerRugRenderState renderState, PoseStack poseStack,
                       SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        final boolean flag = renderState.blockState.getBlock() instanceof PlayerRugBlock;
        final Direction direction = flag ? renderState.blockState.getValue(PlayerRugBlock.FACING) : Direction.UP;
        final RenderType renderType = renderState.renderType;
        final boolean standing = renderState.standing;

        poseStack.pushPose();

        renderRug(nodeCollector, direction, renderType, renderState.isSlim,
                standing, poseStack, renderState.lightCoords, headModel, 0, renderState.breakProgress);

        poseStack.popPose();
    }

    public static void renderRug(SubmitNodeCollector nodeCollector, Direction direction, RenderType renderType,
                                 boolean slim, boolean standing,
                                 PoseStack poseStack, int combinedLight, SkullModel skullModel,
                                 int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.001d, 0.5f);

        // Render head
        poseStack.pushPose();

        poseStack.translate(0, (standing ? 0.4999f : 0f), 0);

        float angle = (direction.get2DDataValue() + 2) * -90f;

        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0, -0.001, standing ? 8f / 16f : -9f / 16f);
        poseStack.scale(-1.0F, -1.0F, 1.0F);


        SkullModelBase.State skullmodelbase$state = new SkullModelBase.State();
        skullmodelbase$state.yRot = 0;

        nodeCollector.submitModel(skullModel, skullmodelbase$state, poseStack, renderType, combinedLight, OverlayTexture.NO_OVERLAY, outlineColor, breakProgress);

        poseStack.popPose();

        // Render body
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));

        if (standing) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.translate(0f, 7f / 16f, -1f / 16f);
        }

        nodeCollector.submitCustomGeometry(poseStack, renderType, (pose, builder) -> {
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
                buildBodyPart(builder, pose,
                        xOffset + (slim ? 1f / 16f : 0f), yOffset, zOffset,
                        (slim ? 3f : 4f) / 16f, thickness, 12f / 16f,
                        (slim ? 39f : 40f) / texWidth, 52f / texHeight, 36f / texWidth, 64f / texHeight,
                        texWidth, texHeight, combinedLight);
            } else {
                buildBodyPart(builder, pose,
                        xOffset, yOffset, zOffset - (slim ? 1f / 16f : 0f),
                        -12f / 16f, thickness, -(slim ? 3f : 4f) / 16f,
                        (slim ? 46f : 48f) / texWidth, 52f / texHeight, (slim ? 43f : 44f) / texWidth, 64f / texHeight,
                        texWidth, texHeight, combinedLight);
            }

            // Right Arm
            xOffset = 12f / 16f - 0.5f;
            zOffset = 1f / 16f - 0.5f;
            if (standing) {
                buildBodyPart(builder, pose,
                        xOffset, yOffset, zOffset,
                        (slim ? 3f : 4f) / 16f, thickness, 12f / 16f,
                        (slim ? 47f : 48f) / texWidth, 20f / texHeight, 44f / texWidth, 32f / texHeight,
                        texWidth, texHeight, combinedLight);
            } else {
                buildBodyPart(builder, pose,
                        xOffset, yOffset, zOffset,
                        12f / 16f, thickness, (slim ? 3f : 4f) / 16f,
                        (slim ? 54f : 56f) / texWidth, 20f / texHeight, (slim ? 51f : 52f) / texWidth, 32f / texHeight,
                        texWidth, texHeight, combinedLight);
            }

            // Body
            xOffset = 0.25f - 0.5f;
            zOffset = 1f / 16f - 0.5f;
            buildBodyPart(builder, pose,
                    xOffset, yOffset, zOffset,
                    8f / 16f, thickness, 12f / 16f,
                    (standing ? 28f : 32f) / texWidth, 20f / texHeight, (standing ? 20f : 40f) / texWidth, 32f / texHeight,
                    texWidth, texHeight, combinedLight);

            // Left Leg
            xOffset = 0.25f - 0.5f;
            zOffset = 13f / 16f - 0.5f;
            buildBodyPart(builder, pose,
                    xOffset, yOffset, zOffset,
                    4f / 16f, thickness, 12f / 16f,
                    (standing ? 20f : 28f) / texWidth, 52f / texHeight, (standing ? 24f : 32f) / texWidth, 64f / texHeight,
                    texWidth, texHeight, combinedLight);

            // Right Leg
            xOffset = 0.0f;
            zOffset = 13f / 16f - 0.5f;
            buildBodyPart(builder, pose,
                    xOffset, yOffset, zOffset,
                    4f / 16f, thickness, 12f / 16f,
                    (standing ? 4f : 12f) / texWidth, 20f / texHeight, (standing ? 8f : 16f) / texWidth, 32f / texHeight,
                    texWidth, texHeight, combinedLight);
        });

        poseStack.popPose();

        poseStack.popPose();
    }

    public RenderType getRenderType(@Nullable ResolvableProfile resolvableProfile) {
        if (resolvableProfile == null)
            return RenderTypes.entityTranslucent(defaultTexture);

        return playerSkinRenderCache.getOrDefault(resolvableProfile).renderType();
    }

    private static Vec3i directionToNormal(Direction direction) {
        return new Vec3i(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    public static void buildBodyPart(VertexConsumer builder, PoseStack.Pose pose, float xPos, float yPos, float zPos, float width, float depth, float length, float minU, float minV, float maxU, float maxV, float texWidth, float texHeight, int combinedLight) {
        Vec3i downVec = directionToNormal(Direction.DOWN);
        Vec3i upVec = directionToNormal(Direction.UP);
        Vec3i northVec = directionToNormal(Direction.NORTH);
        Vec3i southVec = directionToNormal(Direction.SOUTH);
        Vec3i westVec = directionToNormal(Direction.WEST);
        Vec3i eastVec = directionToNormal(Direction.EAST);

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
        builder.addVertex(matrix4f, x, y, z)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(combinedLight)
                .setNormal(pose, directionVec.getX(), directionVec.getY(), directionVec.getZ());
    }
}
