package uk.kihira.playerrugs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class PlayerRugModel<T extends LivingEntity> extends EntityModel<T> {
	private final ModelRenderer bone;
	private final ModelRenderer Head;
	private final ModelRenderer HeadWear;
	private final ModelRenderer Torso;
	private final ModelRenderer LeftArm;
	private final ModelRenderer RightArm;
	private final ModelRenderer LeftLeg;
	private final ModelRenderer RightLeg;

	public PlayerRugModel(boolean slim) {
		super(RenderType::getEntityTranslucent);
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);


		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(Head);
		Head.setTextureOffset(0, 0).addBox(-4.0F, -7.0F, -14.0F, 8.0F, 8.0F, 8.0F, -0.5F, false);

		HeadWear = new ModelRenderer(this);
		HeadWear.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(HeadWear);
		HeadWear.setTextureOffset(32, 0).addBox(-4.0F, -7.0F, -14.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		Torso = new ModelRenderer(this);
		Torso.setRotationPoint(0.0F, 0.0F, 1.0F);
		bone.addChild(Torso);
		setRotationAngle(Torso, 1.5708F, 0.0F, 0.0F);
		Torso.setTextureOffset(24, 20).addBox(-4.0F, -7.0F, 1.0F, 8.0F, 12.0F, 0.0F, 0.0F, false);
		Torso.setTextureOffset(20, 20).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 12.0F, 0.0F, 0.0F, false);
		Torso.setTextureOffset(27, 19).addBox(4.0F, -7.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
		Torso.setTextureOffset(19, 19).addBox(-4.0F, -7.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
		Torso.setTextureOffset(19, 19).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 0.0F, 1.0F, 0.0F, false);

		LeftArm = new ModelRenderer(this);
		RightArm = new ModelRenderer(this);
		if(slim) {
			LeftArm.setRotationPoint(5.0F, 0.0F, 1.0F);
			bone.addChild(LeftArm);
			setRotationAngle(LeftArm, 1.5708F, 1.5708F, 0.0F);
			LeftArm.setTextureOffset(40, 52).addBox(4.0F, -1.0F, 1.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(35, 48).addBox(4.0F, 11.0F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(36, 52).addBox(4.0F, -1.0F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(41, 51).addBox(7.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(35, 51).addBox(4.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);

			RightArm.setRotationPoint(-5.0F, 0.0F, 1.0F);
			bone.addChild(RightArm);
			setRotationAngle(RightArm, 1.5708F, -1.5708F, 0.0F);
			RightArm.setTextureOffset(48, 20).addBox(-7.0F, -1.0F, 1.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
			RightArm.setTextureOffset(43, 16).addBox(-7.0F, 11.0F, 0.0F, 3.0F, 0.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(48, 19).addBox(-7.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(42, 19).addBox(-4.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, true);
			RightArm.setTextureOffset(44, 20).addBox(-7.0F, -1.0F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
		} else {
			LeftArm.setRotationPoint(5.0F, 0.0F, 1.0F);
			bone.addChild(LeftArm);
			setRotationAngle(LeftArm, 1.5708F, 1.5708F, 0.0F);
			LeftArm.setTextureOffset(40, 52).addBox(3.0F, -1.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(35, 48).addBox(3.0F, 11.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(36, 52).addBox(3.0F, -1.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			LeftArm.setTextureOffset(41, 51).addBox(7.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			LeftArm.setTextureOffset(35, 51).addBox(3.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);

			RightArm.setRotationPoint(-5.0F, 0.0F, 1.0F);
			bone.addChild(RightArm);
			setRotationAngle(RightArm, 1.5708F, -1.5708F, 0.0F);
			RightArm.setTextureOffset(48, 20).addBox(-7.0F, -1.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
			RightArm.setTextureOffset(43, 16).addBox(-7.0F, 11.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(48, 19).addBox(-7.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
			RightArm.setTextureOffset(42, 19).addBox(-3.0F, -1.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, true);
			RightArm.setTextureOffset(44, 20).addBox(-7.0F, -1.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		}

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(1.9F, 0.0F, 1.0F);
		bone.addChild(RightLeg);
		setRotationAngle(RightLeg, 1.5708F, 0.0F, 0.0F);
		RightLeg.setTextureOffset(8, 20).addBox(-5.9F, 5.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		RightLeg.setTextureOffset(3, 16).addBox(-5.9F, 17.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, true);
		RightLeg.setTextureOffset(4, 20).addBox(-5.9F, 5.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		RightLeg.setTextureOffset(12, 19).addBox(-5.9F, 5.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, true);

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(-1.9F, 0.0F, 1.0F);
		bone.addChild(LeftLeg);
		setRotationAngle(LeftLeg, 1.5708F, 0.0F, 0.0F);
		LeftLeg.setTextureOffset(24, 52).addBox(1.9F, 5.0F, 1.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
		LeftLeg.setTextureOffset(19, 48).addBox(1.9F, 17.0F, 0.0F, 4.0F, 0.0F, 1.0F, 0.0F, true);
		LeftLeg.setTextureOffset(28, 51).addBox(5.9F, 5.0F, 0.0F, 0.0F, 12.0F, 1.0F, 0.0F, false);
		LeftLeg.setTextureOffset(20, 52).addBox(1.9F, 5.0F, 0.0F, 4.0F, 12.0F, 0.0F, 0.0F, false);
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