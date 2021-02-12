package uk.kihira.playerrugs.client.renderer;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import java.util.concurrent.Callable;

public class PlayerRugISTERProvider {
	public static Callable<ItemStackTileEntityRenderer> playerStatue() {
		return PlayerRugInventoryRenderer::new;
	}
}
