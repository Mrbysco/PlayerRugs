package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class PlayerRugArmorModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelRenderer bone;
	private final ModelRenderer Head;
	private final ModelRenderer HeadWear;
	private final ModelRenderer Torso;
	private final ModelRenderer LeftArm;
	private final ModelRenderer RightArm;
	private final ModelRenderer RightLeg;
	private final ModelRenderer LeftLeg;

	public PlayerRugArmorModel(boolean slim) {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(Head);
		

		HeadWear = new ModelRenderer(this);
		HeadWear.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(HeadWear);
		

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(Torso);
		setRotationAngle(Torso, 1.5708F, 0.0F, 0.0F);
		Torso.setTextureOffset(24, 36).addBox(-4.0F, -7.0F, 1.0F, 8.0F, 12.0F, 0.0F, 0.0F, false);
		Torso.setTextureOffset(20, 36).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 12.0F, 0.0F, 0.0F, false);
		Torso.setTextureOffset(27, 35).addBox(4.0F, -7.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
		Torso.setTextureOffset(19, 35).addBox(-4.0F, -7.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
		Torso.setTextureOffset(19, 35).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 0.0F, 1.0F, 0.0F, false);

		LeftArm = new ModelRenderer(this);
		RightArm = new ModelRenderer(this);
		if(slim) {
			LeftArm.setRotationPoint(4.0F, 0.0F, 2.0F);
			bone.addChild(LeftArm);
			setRotationAngle(LeftArm, 1.5708F, 1.5708F, 0.0F);
			LeftArm.setTextureOffset(56, 52).addBox(4.5F, -1.0F, 1.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(51, 48).addBox(4.5F, 11.0F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(52, 52).addBox(4.5F, -1.0F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(57, 51).addBox(7.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(51, 51).addBox(4.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);

			RightArm.setRotationPoint(-4.0F, 0.0F, 2.0F);
			bone.addChild(RightArm);
			setRotationAngle(RightArm, 1.5708F, -1.5708F, 0.0F);
			RightArm.setTextureOffset(48, 36).addBox(-7.5F, -1.0F, 1.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			RightArm.setTextureOffset(43, 32).addBox(-7.5F, 11.0F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(48, 35).addBox(-7.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(42, 35).addBox(-4.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, true);
			RightArm.setTextureOffset(44, 36).addBox(-7.5F, -1.0F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
		} else {
			LeftArm.setRotationPoint(4.0F, 0.0F, 1.0F);
			bone.addChild(LeftArm);
			setRotationAngle(LeftArm, 1.5708F, 1.5708F, 0.0F);
			LeftArm.setTextureOffset(56, 52).addBox(2.5F, -1.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(51, 48).addBox(2.5F, 11.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(52, 52).addBox(2.5F, -1.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(57, 51).addBox(6.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(51, 51).addBox(2.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);

			RightArm.setRotationPoint(-4.0F, 0.0F, 1.0F);
			bone.addChild(RightArm);
			setRotationAngle(RightArm, 1.5708F, -1.5708F, 0.0F);
			RightArm.setTextureOffset(48, 36).addBox(-6.5F, -1.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			RightArm.setTextureOffset(43, 32).addBox(-6.5F, 11.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(49, 35).addBox(-6.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(42, 35).addBox(-2.5F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, true);
			RightArm.setTextureOffset(44, 36).addBox(-6.5F, -1.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		}

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(1.9F, 0.0F, 1.0F);
		bone.addChild(RightLeg);
		setRotationAngle(RightLeg, 1.5708F, 0.0F, 0.0F);
		RightLeg.setTextureOffset(8, 36).addBox(-5.9F, 4.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		RightLeg.setTextureOffset(3, 32).addBox(-5.9F, 16.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, true);
		RightLeg.setTextureOffset(4, 36).addBox(-5.9F, 4.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		RightLeg.setTextureOffset(12, 35).addBox(-5.9F, 4.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, true);

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(-1.9F, 0.0F, 1.0F);
		bone.addChild(LeftLeg);
		setRotationAngle(LeftLeg, 1.5708F, 0.0F, 0.0F);
		LeftLeg.setTextureOffset(8, 52).addBox(1.9F, 4.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		LeftLeg.setTextureOffset(3, 48).addBox(1.9F, 16.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, true);
		LeftLeg.setTextureOffset(12, 51).addBox(5.9F, 4.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
		LeftLeg.setTextureOffset(4, 52).addBox(1.9F, 4.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
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