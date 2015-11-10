package uk.kihira.playerrugs.client;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.entity.Entity;

public class EnhancedSkullModel extends ModelSkeletonHead {

    public ModelRenderer bipedHeadwear;

    public EnhancedSkullModel(int texXOffset, int texYOffset, int texWidth, int texHeight) {
        super(texXOffset, texYOffset, texWidth, texHeight);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entity);
        skeletonHead.render(scale);
        bipedHeadwear.render(scale);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entity)
    {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entity);
        bipedHeadwear.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
        bipedHeadwear.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
    }
}
