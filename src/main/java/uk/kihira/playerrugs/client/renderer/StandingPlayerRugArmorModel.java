package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class StandingPlayerRugArmorModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelRenderer bone;
	private final ModelRenderer Torso;
	private final ModelRenderer LeftArm;
	private final ModelRenderer BottomLeftArm;
	private final ModelRenderer RightArm;
	private final ModelRenderer BottomRightArm;
	private final ModelRenderer RightLeg;
	private final ModelRenderer BottomRightLeg;
	private final ModelRenderer LeftLeg;
	private final ModelRenderer BottomLeftLeg;

	public StandingPlayerRugArmorModel(boolean slim) {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 5.0F);
		

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(Torso);
		setRotationAngle(Torso, 1.5708F, 0.0F, 0.0F);
		Torso.setTextureOffset(12, 36).addBox(-4.0F, -7.0F, 1.0F, 8.0F, 12.0F, 0.0F, 0.0F, false);
		Torso.setTextureOffset(32, 36).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 12.0F, 0.0F, 0.0F, false);
		Torso.setTextureOffset(19, 35).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 0.0F, 1.0F, 0.0F, true);

		LeftArm = new ModelRenderer(this);
		RightArm = new ModelRenderer(this);
		if(slim) {
			LeftArm.setRotationPoint(-0.5F, 0.0F, -5.0F);
			bone.addChild(LeftArm);
			setRotationAngle(LeftArm, 1.5708F, 0.0F, 0.0F);
			LeftArm.setTextureOffset(41, 36).addBox(4.0F, -1.5F, 1.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(43, 32).addBox(4.0F, 10.5F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(43, 35).addBox(7.0F, -1.5F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(43, 32).addBox(4.0F, -1.5F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, false);

			BottomLeftArm = new ModelRenderer(this);
			BottomLeftArm.setRotationPoint(6.0F, 0.0F, 0.0F);
			LeftArm.addChild(BottomLeftArm);
			setRotationAngle(BottomLeftArm, 0.0F, 3.1416F, 0.0F);
			BottomLeftArm.setTextureOffset(51, 36).addBox(-1.0F, -1.5F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);

			RightArm.setRotationPoint(-0.5F, 0.0F, -5.0F);
			bone.addChild(RightArm);
			setRotationAngle(RightArm, 1.5708F, 0.0F, 0.0F);
			RightArm.setTextureOffset(49, 52).addBox(-6.0F, -1.5F, 1.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			RightArm.setTextureOffset(51, 48).addBox(-6.0F, 10.5F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(57, 51).addBox(-6.0F, -1.5F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(51, 48).addBox(-6.0F, -1.5F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, true);

			BottomRightArm = new ModelRenderer(this);
			BottomRightArm.setRotationPoint(-5.0F, 0.0F, 0.0F);
			RightArm.addChild(BottomRightArm);
			setRotationAngle(BottomRightArm, 0.0F, 3.1416F, 0.0F);
			BottomRightArm.setTextureOffset(56, 52).addBox(-2.0F, -1.5F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
		} else {
			LeftArm.setRotationPoint(0.5F, 0.0F, -5.0F);
			bone.addChild(LeftArm);
			setRotationAngle(LeftArm, 1.5708F, 0.0F, 0.0F);
			LeftArm.setTextureOffset(40, 36).addBox(3.0F, -1.5F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(43, 32).addBox(3.0F, 10.5F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(43, 35).addBox(7.0F, -1.5F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(43, 32).addBox(3.0F, -1.5F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);

			BottomLeftArm = new ModelRenderer(this);
			BottomLeftArm.setRotationPoint(5.0F, 0.0F, 0.0F);
			LeftArm.addChild(BottomLeftArm);
			setRotationAngle(BottomLeftArm, 0.0F, 3.1416F, 0.0F);
			BottomLeftArm.setTextureOffset(52, 36).addBox(-2.0F, -1.5F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);

			RightArm.setRotationPoint(-0.5F, 0.0F, -5.0F);
			bone.addChild(RightArm);
			setRotationAngle(RightArm, 1.5708F, 0.0F, 0.0F);
			RightArm.setTextureOffset(48, 52).addBox(-7.0F, -1.5F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			RightArm.setTextureOffset(51, 48).addBox(-7.0F, 10.5F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(59, 51).addBox(-7.0F, -1.5F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(51, 48).addBox(-7.0F, -1.5F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, true);

			BottomRightArm = new ModelRenderer(this);
			BottomRightArm.setRotationPoint(-5.0F, 0.0F, 0.0F);
			RightArm.addChild(BottomRightArm);
			setRotationAngle(BottomRightArm, 0.0F, 3.1416F, 0.0F);
			BottomRightArm.setTextureOffset(56, 52).addBox(-2.0F, -1.5F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		}

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(1.9F, 0.0F, 0.0F);
		bone.addChild(RightLeg);
		setRotationAngle(RightLeg, 1.5708F, 0.0F, 0.0F);
		RightLeg.setTextureOffset(0, 52).addBox(-5.9F, 4.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		RightLeg.setTextureOffset(3, 48).addBox(-5.9F, 16.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, true);
		RightLeg.setTextureOffset(12, 51).addBox(-5.9F, 4.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, true);

		BottomRightLeg = new ModelRenderer(this);
		BottomRightLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
		RightLeg.addChild(BottomRightLeg);
		setRotationAngle(BottomRightLeg, 0.0F, 3.1416F, 0.0F);
		BottomRightLeg.setTextureOffset(8, 52).addBox(-4.0F, 4.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
		bone.addChild(LeftLeg);
		setRotationAngle(LeftLeg, 1.5708F, 0.0F, 0.0F);
		LeftLeg.setTextureOffset(0, 36).addBox(1.9F, 4.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		LeftLeg.setTextureOffset(3, 32).addBox(1.9F, 16.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, true);
		LeftLeg.setTextureOffset(11, 35).addBox(5.9F, 4.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);

		BottomLeftLeg = new ModelRenderer(this);
		BottomLeftLeg.setRotationPoint(1.9F, 0.0F, 0.0F);
		LeftLeg.addChild(BottomLeftLeg);
		setRotationAngle(BottomLeftLeg, 0.0F, 3.1416F, 0.0F);
		BottomLeftLeg.setTextureOffset(8, 36).addBox(0.0F, 4.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
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