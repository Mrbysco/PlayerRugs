package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import uk.kihira.playerrugs.common.blockentity.PlayerRugBlockEntity;

public class PlayerRugInventoryRenderer extends BlockEntityWithoutLevelRenderer {
    public final SkullModel headModel;
    public boolean isSlim = false;

    public PlayerRugInventoryRenderer(BlockEntityRendererProvider.Context context) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.headModel = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!stack.isEmpty()) {
            ResolvableProfile resolvableProfile = stack.get(DataComponents.PROFILE);
            if (resolvableProfile != null && !resolvableProfile.isResolved()) {
                stack.remove(DataComponents.PROFILE);
                PlayerRugBlockEntity.resolve(resolvableProfile).thenAcceptAsync(profile ->
                        stack.set(DataComponents.PROFILE, profile), Minecraft.getInstance());
            }

            SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
            if (resolvableProfile != null && isSlim != skinmanager.getInsecureSkin(resolvableProfile.gameProfile()).model().id().equals("slim"))
                isSlim = !isSlim;

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
            //Render the inventory model as slim (until we figure out how to get the model type from the resolvableProfile)
            PlayerRugBER.renderRug(Direction.NORTH, resolvableProfile, isSlim, false, poseStack, buffer, packedLight, this.headModel);
            poseStack.popPose();
        }
    }
}