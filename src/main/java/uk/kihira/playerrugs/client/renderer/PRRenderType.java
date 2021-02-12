package uk.kihira.playerrugs.client.renderer;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PRRenderType extends RenderType {
	public PRRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType playerRugRenderer(ResourceLocation locationIn) {
		RenderType.State rendertype$state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(locationIn, false, false))
				.transparency(NO_TRANSPARENCY)
				.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
				.alpha(DEFAULT_ALPHA)
				.cull(CULL_DISABLED)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.build(true);
		return makeType("player_rug_renderer", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, false, rendertype$state);
	}
}
