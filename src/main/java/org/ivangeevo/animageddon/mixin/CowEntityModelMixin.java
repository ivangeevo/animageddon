package org.ivangeevo.animageddon.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityModelAdded;

@Mixin(CowEntityModel.class)
public abstract class CowEntityModelMixin<T extends Entity> extends QuadrupedEntityModel<T> implements CowEntityModelAdded
{
    protected CowEntityModelMixin(ModelPart root, boolean headScaled, float childHeadYOffset, float childHeadZOffset, float invertedChildHeadScale, float invertedChildBodyScale, int childBodyYOffset)
    {
        super(root, headScaled, childHeadYOffset, childHeadZOffset, invertedChildHeadScale, invertedChildBodyScale, childBodyYOffset);
    }

    @Inject(method = "getTexturedModelData", at = @At("HEAD"), cancellable = true)
    private static void injectedGetTexturedModelData(CallbackInfoReturnable<TexturedModelData> cir)
    {

        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 12;
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).uv(22, 0).cuboid(EntityModelPartNames.RIGHT_HORN, -5.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f).uv(22, 0).cuboid(EntityModelPartNames.LEFT_HORN, 4.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f), ModelTransform.pivot(0.0f, 4.0f, -8.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(18, 4).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f).uv(52, 0).cuboid(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f), ModelTransform.of(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 12.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(4.0f, 12.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 12.0f, -6.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(4.0f, 12.0f, -6.0f));

        CowEntityAdded cowEntity = null;

        try {
            // Attempt to cast the entity to CowEntityAdded
            cowEntity = (CowEntityAdded) Thread.currentThread().getContextClassLoader().loadClass("net.minecraft.client.model.CowEntityModel")
                    .getDeclaredField("this$0").get(null);
        } catch (Exception e) {
            // Handle the exception (e.g., class not found or casting failed)
            e.printStackTrace();
        }

        if (cowEntity != null && (cowEntity.gotMilk())) {

            modelPartData.addChild("udder", ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f), ModelTransform.pivot(0.0f, 4.0f, -8.0f));

            cir.setReturnValue(TexturedModelData.of(modelData, 64, 32));
        }
    }

}