package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class StandingPlayerRugModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelRenderer bone;
	private final ModelRenderer Head;
	private final ModelRenderer HeadWear;
	private final ModelRenderer Torso;
	private final ModelRenderer LeftArm;
	private final ModelRenderer RightArm;
	private final ModelRenderer LeftLeg;
	private final ModelRenderer RightLeg;

	public StandingPlayerRugModel(boolean slim) {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(Head);
		setRotationAngle(Head, -1.5708F, 3.1416F, 0.0F);
		Head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -5.0F, 8.0F, 8.0F, 8.0F, -0.5F, false);

		HeadWear = new ModelRenderer(this);
		HeadWear.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(HeadWear);
		setRotationAngle(HeadWear, -1.5708F, 3.1416F, 0.0F);
		HeadWear.setTextureOffset(32, 0).addBox(-4.0F, -10.0F, -5.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, -1.0F, 4.0F);
		bone.addChild(Torso);
		setRotationAngle(Torso, 1.5708F, 0.0F, -3.1416F);
		Torso.setTextureOffset(19, 19).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 12.0F, 1.0F, 0.0F, false);

		LeftArm = new ModelRenderer(this);
		LeftArm.setRotationPoint(7.0F, -1.0F, 4.0F);
		bone.addChild(LeftArm);
		setRotationAngle(LeftArm, 1.5708F, 0.0F, -3.1416F);
		if(slim) {
			LeftArm.setTextureOffset(35, 51).addBox(11.0F, -6.0F, 0.0F, 3.0F, 12.0F, 1.0F, 0.0F, false);
		} else {
			LeftArm.setTextureOffset(35, 51).addBox(11.0F, -6.0F, 0.0F, 4.0F, 12.0F, 1.0F, 0.0F, false);
		}

		RightArm = new ModelRenderer(this);
		RightArm.setRotationPoint(-15.0F, -1.0F, 1.0F);
		bone.addChild(RightArm);
		setRotationAngle(RightArm, 1.5708F, 0.0F, -3.1416F);
		if(slim) {
			RightArm.setTextureOffset(43, 19).addBox(-22.0F, -3.0F, 0.0F, 3.0F, 12.0F, 1.0F, 0.0F, true);
		} else {
			RightArm.setTextureOffset(43, 19).addBox(-23.0F, -3.0F, 0.0F, 4.0F, 12.0F, 1.0F, 0.0F, true);
		}


		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(1.9F, 0.0F, 1.0F);
		bone.addChild(LeftLeg);
		setRotationAngle(LeftLeg, 1.5708F, 0.0F, -3.1416F);
		LeftLeg.setTextureOffset(19, 51).addBox(-1.9F, 9.0F, -1.0F, 4.0F, 12.0F, 1.0F, 0.0F, true);

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(2.1F, 0.0F, 1.0F);
		bone.addChild(RightLeg);
		setRotationAngle(RightLeg, 1.5708F, 0.0F, -3.1416F);
		RightLeg.setTextureOffset(3, 19).addBox(1.9F, 9.0F, -1.0F, 4.0F, 12.0F, 1.0F, 0.0F, true);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bone.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}