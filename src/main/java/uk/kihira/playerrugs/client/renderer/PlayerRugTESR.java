package uk.kihira.playerrugs.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import uk.kihira.playerrugs.common.blocks.PlayerRugBlock;
import uk.kihira.playerrugs.common.tileentities.PlayerRugTE;

import javax.annotation.Nullable;
import java.util.Map;

public class PlayerRugTESR extends TileEntityRenderer<PlayerRugTE> {
    public static final PlayerRugModel model = new PlayerRugModel(false);
    public static final PlayerRugModel slimModel = new PlayerRugModel(true);
    public static final StandingPlayerRugModel standingModel = new StandingPlayerRugModel(false);
    public static final StandingPlayerRugModel standingSlimModel = new StandingPlayerRugModel(true);

    public static final PlayerRugArmorModel armorModel = new PlayerRugArmorModel(false);
    public static final PlayerRugArmorModel slimArmorModel = new PlayerRugArmorModel(true);
    public static final StandingPlayerRugArmorModel standingArmorModel = new StandingPlayerRugArmorModel(false);
    public static final StandingPlayerRugArmorModel standingSlimArmorModel = new StandingPlayerRugArmorModel(true);

    public static final ResourceLocation defaultTexture = DefaultPlayerSkin.getDefaultSkinLegacy();

    public PlayerRugTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(PlayerRugTE te, float v, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        final BlockState blockstate = te.getBlockState();
        final boolean flag = blockstate.getBlock() instanceof PlayerRugBlock;
        final Direction direction = flag ? blockstate.get(PlayerRugBlock.FACING) : Direction.UP;
        final boolean standing = flag ? blockstate.get(PlayerRugBlock.STANDING) : false;
        final GameProfile profile = te.getPlayerProfile();

        render(direction, profile, matrixStackIn, bufferIn, combinedLightIn, standing);
    }

    public static void render(@Nullable Direction direction, @Nullable GameProfile profile, MatrixStack matrix, IRenderTypeBuffer typeBuffer, int combinedLight, boolean standing) {
        final boolean isSlim = profile != null && (profile.getId().hashCode() & 1) == 1;

        final EntityModel rugModel = isSlim ? (standing ? standingSlimModel : slimModel) : standing ? standingModel : model;
        final EntityModel rugArmorModel = isSlim ? (standing ? standingSlimArmorModel : slimArmorModel) : standing ? standingArmorModel : armorModel;

        // Render head
        matrix.translate(0.5D, 0.25D, 0.5D);
        matrix.push();
        if (direction != null) {
            switch(direction) {
                case NORTH:
                    matrix.rotate(Vector3f.YP.rotationDegrees(standing ? 180 : 0));
                    break;
                case SOUTH:
                    matrix.rotate(Vector3f.YP.rotationDegrees(standing ? 0 : 180));
                    break;
                case WEST:
                    matrix.rotate(Vector3f.YP.rotationDegrees(standing ? -90 : 90));
                    break;
                default:
                    matrix.rotate(Vector3f.YP.rotationDegrees(standing ? 90 : 270));
            }
            matrix.translate(0, standing ? 0.2 : 0.25, -0.25);
            matrix.rotate(Vector3f.XN.rotationDegrees(standing ? -90 : 0));
        }
        matrix.scale(-1.0F, -1.0F, 1.0F);
        matrix.translate(0.0D, standing ? -1.25D : -1.0, 0.0D);

        final IVertexBuilder ivertexbuilder = typeBuffer.getBuffer(getRenderType(profile));
        rugModel.render(matrix, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrix.scale(1.125F, standing ? 1.3125F : 1.25F, 1.125F);
        matrix.translate(0.0D, standing ? -0.3515625D : -0.296875D, 0.0D);

        rugArmorModel.render(matrix, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrix.pop();
    }

    public static void renderItem(TransformType type, @Nullable Direction direction, @Nullable GameProfile profile, MatrixStack matrix, IRenderTypeBuffer typeBuffer, int combinedLight) {
        final boolean isSlim = profile != null && (profile.getId().hashCode() & 1) == 1;
        final EntityModel rugModel = isSlim ? slimModel : model;
        final EntityModel rugArmorModel = isSlim ? slimArmorModel : armorModel;

        // Render head
        matrix.translate(0.5D, 0.25D, 0.5D);
        matrix.push();
        if (direction != null) {
            switch(direction) {
                case NORTH:
                    matrix.rotate(Vector3f.YP.rotationDegrees(0));
                    break;
                case SOUTH:
                    matrix.rotate(Vector3f.YP.rotationDegrees(180));
                    break;
                case WEST:
                    matrix.rotate(Vector3f.YP.rotationDegrees(90));
                    break;
                default:
                    matrix.rotate(Vector3f.YP.rotationDegrees(270));
            }
            matrix.translate(0, 0.25, -0.25);
            matrix.rotate(Vector3f.XN.rotationDegrees(0));
        }
        matrix.translate(0.0D, -0.25, 0.0D);

        if(type == TransformType.GUI) {
            matrix.rotate(Vector3f.XN.rotationDegrees(-45));
            matrix.rotate(Vector3f.YP.rotationDegrees(45));
            matrix.scale(0.5F, 0.5F, 0.5F);
            matrix.translate(0.0D, 0.75D, 0.0D);
        }
        if(type == TransformType.GROUND) {
            matrix.scale(0.5F, 0.5F, 0.5F);
            matrix.translate(0,0.25,0);
        }
        if(type == TransformType.FIRST_PERSON_RIGHT_HAND) {
            matrix.rotate(Vector3f.XN.rotationDegrees(-15));
            matrix.rotate(Vector3f.YP.rotationDegrees(15));
            matrix.scale(0.75F, 0.75F, 0.75F);
            matrix.translate(0.75,0.25,0);
        }
        if(type == TransformType.FIRST_PERSON_LEFT_HAND) {
            matrix.rotate(Vector3f.XN.rotationDegrees(-15));
            matrix.rotate(Vector3f.YP.rotationDegrees(-15));
            matrix.scale(0.75F, 0.75F, 0.75F);
            matrix.translate(-0.75,0.25,0);
        }
        if(type == TransformType.THIRD_PERSON_RIGHT_HAND) {
            matrix.scale(0.75F, 0.75F, 0.75F);
            matrix.rotate(Vector3f.XN.rotationDegrees(-90));
            matrix.translate(0,0.5,0);
        }
        if(type == TransformType.THIRD_PERSON_LEFT_HAND) {
            matrix.scale(0.75F, 0.75F, 0.75F);
            matrix.rotate(Vector3f.XN.rotationDegrees(-90));
            matrix.translate(0,0.5,0);
        }
        if(type == TransformType.FIXED) {
            matrix.scale(0.5F, 0.5F, 0.5F);
            matrix.translate(0,0.6125,0.375);
            matrix.rotate(Vector3f.XN.rotationDegrees(90));
            matrix.rotate(Vector3f.YP.rotationDegrees(180));
        }

        matrix.scale(-1.0F, -1.0F, 1.0F);
        matrix.translate(0.0D, -1.25D, 0.0D);

        final IVertexBuilder ivertexbuilder = typeBuffer.getBuffer(getRenderType(profile));
        rugModel.render(matrix, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrix.scale(1.125F, 1.25F, 1.125F);
        matrix.translate(0.0D, -0.296875D, 0.0D);

        rugArmorModel.render(matrix, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrix.pop();
    }

    private static RenderType getRenderType(@Nullable GameProfile gameProfileIn) {
        if(gameProfileIn != null) {
            Minecraft minecraft = Minecraft.getInstance();
            Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(gameProfileIn);
            if (map.containsKey(Type.SKIN)) {
                return RenderType.getEntityTranslucentCull(minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN));
            } else {
                return RenderType.getEntityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(gameProfileIn)));
            }
        } else {
            return RenderType.getEntityCutoutNoCull(defaultTexture);
        }
    }
}
