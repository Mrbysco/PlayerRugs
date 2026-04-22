package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class PlayerRugInventoryRenderer implements SpecialModelRenderer<PlayerSkinRenderCache.RenderInfo> {
    private final PlayerSkinRenderCache playerSkinRenderCache;
    public final SkullModel headModel;

    public PlayerRugInventoryRenderer(PlayerSkinRenderCache playerSkinRenderCache, SkullModel headModel) {
        this.playerSkinRenderCache = playerSkinRenderCache;
        this.headModel = headModel;
    }

	@Override
	public void submit(@Nullable PlayerSkinRenderCache.RenderInfo argument, PoseStack poseStack,
	                   SubmitNodeCollector nodeCollector, int packedLight,
	                   int packedOverlay, boolean hasFoil, int outlineColor) {
        RenderType renderType = argument != null ? argument.renderType() : PlayerSkinRenderCache.DEFAULT_PLAYER_SKIN_RENDER_TYPE;
        boolean isSlim = argument != null && argument.playerSkin().model() == PlayerModelType.SLIM;

        poseStack.pushPose();
        poseStack.translate(3f / 16f, 3f / 16f, 0.5D);

		poseStack.mulPose(Axis.XN.rotationDegrees(-45));
		poseStack.mulPose(Axis.YP.rotationDegrees(45));
		poseStack.scale(0.5F, 0.5F, 0.5F);
		poseStack.translate(0.0D, 0.75D, 0.0D);
//
//        if (type == ItemDisplayContext.GUI) {
//            poseStack.mulPose(Axis.XN.rotationDegrees(-45));
//            poseStack.mulPose(Axis.YP.rotationDegrees(45));
//            poseStack.scale(0.5F, 0.5F, 0.5F);
//            poseStack.translate(0.0D, 0.75D, 0.0D);
//        }
//        if (type == ItemDisplayContext.GROUND) {
//            poseStack.translate(1f / 16f, 0, -0.25);
//            poseStack.scale(0.5F, 0.5F, 0.5F);
//            poseStack.translate(0, 0.25, 0);
//        }
//        if (type == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
//            poseStack.mulPose(Axis.XN.rotationDegrees(-15));
//            poseStack.mulPose(Axis.YP.rotationDegrees(15));
//            poseStack.scale(0.5F, 0.5F, 0.5F);
//            poseStack.translate(6f / 16f, 1, 0);
//        }
//        if (type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
//            poseStack.mulPose(Axis.XN.rotationDegrees(-15));
//            poseStack.mulPose(Axis.YP.rotationDegrees(-15));
//            poseStack.scale(0.5F, 0.5F, 0.5F);
//            poseStack.translate(0, 1, -(6f / 16f));
//        }
//        if (type == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
//            poseStack.scale(0.75F, 0.75F, 0.75F);
//            poseStack.mulPose(Axis.XN.rotationDegrees(-90));
//            poseStack.translate(-(1f / 16f), -(1f / 16f), -(10f / 16f));
//        }
//        if (type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
//            poseStack.scale(0.75F, 0.75F, 0.75F);
//            poseStack.mulPose(Axis.XN.rotationDegrees(-90));
//            poseStack.translate(-(1f / 16f), -(1f / 16f), -(10f / 16f));
//        }
//        if (type == ItemDisplayContext.FIXED) {
//            poseStack.scale(0.5F, 0.5F, 0.5F);
//            poseStack.translate(18f / 16f, 18f / 16f, 2f / 16f);
//            poseStack.mulPose(Axis.XN.rotationDegrees(90));
//            poseStack.mulPose(Axis.YP.rotationDegrees(180));
//        }
        //Render the inventory model as slim (until we figure out how to get the model type from the resolvableProfile)
        PlayerRugBER.renderRug(nodeCollector, Direction.NORTH, renderType,
                isSlim, false, poseStack, packedLight, this.headModel, outlineColor, null);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack posestack = new PoseStack();
        this.headModel.root().getExtentsForGui(posestack, output);
    }

    @Override
    public PlayerSkinRenderCache.RenderInfo extractArgument(ItemStack stack) {
        ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
        return resolvableprofile == null ? null : this.playerSkinRenderCache.getOrDefault(resolvableprofile);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<PlayerSkinRenderCache.RenderInfo> {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<PlayerRugInventoryRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @NotNull
        @Override
        public MapCodec<PlayerRugInventoryRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @NotNull
        @Override
        public PlayerRugInventoryRenderer bake(SpecialModelRenderer.BakingContext context) {
            final EntityModelSet entityModelSet = context.entityModelSet();
            SkullModel skullModel = new SkullModel(entityModelSet.bakeLayer(ModelLayers.PLAYER_HEAD));
            return new PlayerRugInventoryRenderer(context.playerSkinRenderCache(), skullModel);
        }
    }
}