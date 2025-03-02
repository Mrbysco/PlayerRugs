package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class PRRenderType extends RenderType {
	public PRRenderType(String nameIn, VertexFormat formatIn, Mode drawMode, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawMode, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType playerRug(ResourceLocation location) {
		return create("player_rug",
				DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 256, true, false,
				CompositeState.builder()
						.setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
						.setTextureState(new TextureStateShard(location, false, false))
						.setTransparencyState(NO_TRANSPARENCY)
						.setCullState(NO_CULL)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(true));
	}
}
