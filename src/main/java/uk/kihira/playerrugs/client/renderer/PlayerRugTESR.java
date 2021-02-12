package uk.kihira.playerrugs.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import uk.kihira.playerrugs.common.blocks.PlayerRugBlock;
import uk.kihira.playerrugs.common.tileentities.PlayerRugTE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class PlayerRugTESR extends TileEntityRenderer<PlayerRugTE> {
	public static final HumanoidHeadModel headModel = new HumanoidHeadModel();
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

		this.render(direction, profile, matrixStackIn, bufferIn, combinedLightIn, standing);
	}

	public static void render(@Nonnull Direction direction, @Nullable GameProfile profile, MatrixStack matrix, IRenderTypeBuffer typeBuffer, int combinedLight, boolean standing) {
		final boolean slimModel = profile != null && (profile.getId().hashCode() & 1) == 1;

		matrix.translate(0.5f, 0.001d, 0.5f);
		// Render head
		matrix.push();
		RenderSystem.enableRescaleNormal();

		matrix.translate(0, (standing ? 0.4999f: 0f), 0);

		float angle = (direction.getHorizontalIndex()+2)*-90f;

		matrix.rotate(Vector3f.YP.rotationDegrees(angle));
		matrix.translate(0, -0.001, standing ? 8f/16f: -9f/16f);
		matrix.scale(-1.0F, -1.0F, 1.0F);

		IVertexBuilder ivertexbuilder = typeBuffer.getBuffer(getRenderType(profile));
		headModel.render(matrix, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

		RenderSystem.disableRescaleNormal();
		matrix.pop();

		matrix.push();
		matrix.rotate(Vector3f.YP.rotationDegrees(angle));

		if (standing) {
			matrix.rotate(Vector3f.XP.rotationDegrees(90f));
			matrix.translate(0f, 7f/16f, -1f/16f);
		}

		if (typeBuffer instanceof IRenderTypeBuffer.Impl) {
			((IRenderTypeBuffer.Impl) typeBuffer).finish();
		}

		IVertexBuilder builder = typeBuffer.getBuffer(PRRenderType.playerRugRenderer(getSkinLocation(profile)));

		float texHeight = 64;
		float texWidth = 64;
		float xOffset = 4f/16f-0.5f;
		float zOffset = 5f/16f-0.5f;
		float thickness = 1f/16f;
		float yOffset = 1f/16f;

		// Left Arm
		if (standing) {
			xOffset = -0.5f;
			zOffset = 1f/16f-0.5f;
			buildBodyPart(builder, matrix.getLast(),
					xOffset+(slimModel?1f/16f:0f), yOffset, zOffset,
					(slimModel?3f:4f)/16f, thickness, 12f/16f,
					(slimModel?39f:40f)/texWidth, 52f/texHeight, 36f/texWidth, 64f/texHeight,
					texWidth, texHeight, combinedLight);
		}
		else {
			buildBodyPart(builder, matrix.getLast(),
					xOffset, yOffset, zOffset-(slimModel?1f/16f:0f),
					-12f/16f, thickness, -(slimModel?3f:4f)/16f,
					(slimModel?46f:48f)/texWidth, 52f/texHeight, (slimModel?43f:44f)/texWidth, 64f/texHeight,
					texWidth, texHeight, combinedLight);
		}

		// Right Arm
		xOffset = 12f/16f-0.5f;
		zOffset = 1f/16f-0.5f;
		if (standing) {
			buildBodyPart(builder, matrix.getLast(),
					xOffset, yOffset, zOffset,
					(slimModel?3f:4f)/16f, thickness, 12f/16f,
					(slimModel?47f:48f)/texWidth, 20f/texHeight, 44f/texWidth, 32f/texHeight,
					texWidth, texHeight, combinedLight);
		}
		else {
			buildBodyPart(builder, matrix.getLast(),
					xOffset, yOffset, zOffset,
					12f/16f, thickness, (slimModel?3f:4f)/16f,
					(slimModel?54f:56f)/texWidth, 20f/texHeight, (slimModel?51f:52f)/texWidth, 32f/texHeight,
					texWidth, texHeight, combinedLight);
		}

		// Body
		xOffset = 0.25f-0.5f;
		zOffset = 1f/16f-0.5f;
		buildBodyPart(builder, matrix.getLast(),
				xOffset, yOffset, zOffset,
				8f/16f, thickness, 12f/16f,
				(standing?28f:32f)/texWidth, 20f/texHeight, (standing?20f:40f)/texWidth, 32f/texHeight,
				texWidth, texHeight, combinedLight);

		// Left Leg
		xOffset = 0.25f-0.5f;
		zOffset = 13f/16f-0.5f;
		buildBodyPart(builder, matrix.getLast(),
				xOffset, yOffset, zOffset,
				4f/16f, thickness, 12f/16f,
				(standing?20f:28f)/texWidth, 52f/texHeight, (standing?24f:32f)/texWidth, 64f/texHeight,
				texWidth, texHeight, combinedLight);

		// Right Leg
		xOffset = 0.5f-0.5f;
		zOffset = 13f/16f-0.5f;
		buildBodyPart(builder, matrix.getLast(),
				xOffset, yOffset, zOffset,
				4f/16f, thickness, 12f/16f,
				(standing ? 4f : 12f)/texWidth, 20f/texHeight, (standing ? 8f : 16f)/texWidth, 32f/texHeight,
				texWidth, texHeight, combinedLight);

		if (typeBuffer instanceof IRenderTypeBuffer.Impl) {
			((IRenderTypeBuffer.Impl) typeBuffer).finish();
		}

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

	private static ResourceLocation getSkinLocation(GameProfile gameProfileIn) {
		if(gameProfileIn != null) {
			Minecraft minecraft = Minecraft.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(gameProfileIn);
			if (map.containsKey(Type.SKIN)) {
				return minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
			} else {
				return DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(gameProfileIn));
			}
		} else {
			return defaultTexture;
		}
	}

	private static void buildBodyPart(IVertexBuilder builder, MatrixStack.Entry matrixStack, float xPos, float yPos, float zPos, float width, float depth, float length, float minU, float minV, float maxU, float maxV, float texWidth, float texHeight, int combinedLight) {
		Vector3i downVec = Direction.DOWN.getDirectionVec();
		Vector3i upVec = Direction.UP.getDirectionVec();
		Vector3i northVec = Direction.NORTH.getDirectionVec();
		Vector3i southVec = Direction.SOUTH.getDirectionVec();
		Vector3i westVec = Direction.WEST.getDirectionVec();
		Vector3i eastVec = Direction.EAST.getDirectionVec();

		float texDepth = depth*16f;
		// This if is used if texture should be rotated as width would be longer then length (used for arms)
		if (Math.abs(width) > Math.abs(length)) {
			// Draws base texture
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos, minU, minV, combinedLight, upVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos+length, maxU, minV, combinedLight, upVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos+length, maxU, maxV, combinedLight, upVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos, minU, maxV, combinedLight, upVec);

			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos, minU, maxV, combinedLight, downVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos+length, maxU, maxV, combinedLight, downVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos+length, maxU, minV, combinedLight, downVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos, minU, minV, combinedLight, downVec);

			// Draws sides
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos, minU, minV+(texDepth/texHeight), combinedLight, northVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos+length, maxU, minV+(texDepth/texHeight), combinedLight, northVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos+length, maxU, minV, combinedLight, northVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos, minU, minV, combinedLight, northVec);

			float uUpper = maxU + ((minU > maxU) ? (texDepth/texWidth) : -(texDepth/texWidth));
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos+length, uUpper, minV, combinedLight, eastVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos+length, uUpper, maxV, combinedLight, eastVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos+length, maxU, maxV, combinedLight, eastVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos+length, maxU, minV, combinedLight, eastVec);

			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos+length, maxU, maxV-(texDepth/texHeight), combinedLight, southVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos, minU, maxV-(texDepth/texHeight), combinedLight, southVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos, minU, maxV, combinedLight, southVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos+length, maxU, maxV, combinedLight, southVec);

			uUpper = minU + ((minU < maxU) ? (texDepth/texWidth) : -(texDepth/texWidth));
			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos, minU, maxV, combinedLight, westVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos, minU, minV, combinedLight, westVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos, uUpper, minV, combinedLight, westVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos, uUpper, maxV, combinedLight, westVec);
		}
		else {
			// Draws base texture
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos, minU, minV, combinedLight, upVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos+length, minU, maxV, combinedLight, upVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos+length, maxU, maxV, combinedLight, upVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos, maxU, minV, combinedLight, upVec);

			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos, maxU, minV, combinedLight, downVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos+length, maxU, maxV, combinedLight, downVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos+length, minU, maxV, combinedLight, downVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos, minU, minV, combinedLight, downVec);

			// Draws sides
			float uUpper = minU + ((minU < maxU) ? (texDepth/texWidth) : -(texDepth/texWidth));
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos, minU, minV, combinedLight, northVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos+length, minU, maxV, combinedLight, northVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos+length, uUpper, maxV, combinedLight, northVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos, uUpper, minV, combinedLight, northVec);

			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos+length, minU, maxV, combinedLight, eastVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos+length, maxU, maxV, combinedLight, eastVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos+length, maxU, maxV-(texDepth/texHeight), combinedLight, eastVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos+length, minU, maxV-(texDepth/texHeight), combinedLight, eastVec);

			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos+length, maxU, maxV, combinedLight, southVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos, maxU, minV, combinedLight, southVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos, maxU-(texDepth/texWidth), minV, combinedLight, southVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos+length, maxU-(texDepth/texWidth), maxV, combinedLight, southVec);

			addVertexWithUV(builder, matrixStack, xPos+width, yPos-depth, zPos, maxU, minV, combinedLight, westVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos-depth, zPos, minU, minV, combinedLight, westVec);
			addVertexWithUV(builder, matrixStack, xPos, yPos, zPos, minU, minV+(texDepth/texHeight), combinedLight, westVec);
			addVertexWithUV(builder, matrixStack, xPos+width, yPos, zPos, maxU, minV+(texDepth/texHeight), combinedLight, westVec);
		}
	}

	private static void addVertexWithUV(IVertexBuilder builder, MatrixStack.Entry matrixStack, float x, float y, float z, float u, float v, int combinedLight, Vector3i directionVec) {
		Matrix4f matrix4f = matrixStack.getMatrix();
		Matrix3f matrix3f = matrixStack.getNormal();
		builder.pos(matrix4f, x, y, z)
				.color(1.0F, 1.0F, 1.0F, 1.0F)
				.tex(u, v)
				.overlay(OverlayTexture.NO_OVERLAY)
				.lightmap(combinedLight)
				.normal(matrix3f, directionVec.getX(), directionVec.getY(), directionVec.getZ())
				.endVertex();
	}
}
