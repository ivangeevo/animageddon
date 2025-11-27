package org.ivangeevo.animageddon.mixin.client;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntityModel.class)
public abstract class CowEntityModelMixin<T extends Entity> {

    //@Inject(method = "getTexturedModelData", at = @At("HEAD"), cancellable = true)
    private static void customModel(CallbackInfoReturnable<TexturedModelData> cir) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 12;
        modelPartData.addChild(
                EntityModelPartNames.HEAD,
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
                        .uv(22, 0)
                        .cuboid(EntityModelPartNames.RIGHT_HORN, -5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F)
                        .uv(22, 0)
                        .cuboid(EntityModelPartNames.LEFT_HORN, 4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F),
                ModelTransform.pivot(0.0F, 4.0F, -8.0F)
        );
        modelPartData.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(18, 4)
                        .cuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F).uv(52, 0),
                ModelTransform.of(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        modelPartData.addChild("udder",
                ModelPartBuilder.create()
                        .uv(52, 0).cuboid(-2.0F, 0.0F, -0.5F, 4.0F, 8.0F, 1.0F), // full udder
                ModelTransform.pivot(0.0F, 13.0F, 2.5F) // aligns roughly below the body
        );

        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16)
                .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.0F, 12.0F, 7.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(4.0F, 12.0F, 7.0F));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-4.0F, 12.0F, -6.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(4.0F, 12.0F, -6.0F));
        cir.setReturnValue(TexturedModelData.of(modelData, 64, 32));

    }

    //@ModifyReturnValue(method = "getTexturedModelData", at = @At("RETURN"))
    private static TexturedModelData modifyUdderSize(TexturedModelData original) {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // head
        root.addChild("head", ModelPartBuilder.create()
            .uv(0, 0).cuboid(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
            .uv(22, 0).cuboid("right_horn", -5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F)
            .uv(22, 0).cuboid("left_horn", 4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F),
            ModelTransform.pivot(0.0F, 4.0F, -8.0F));

        // body with modified udder (elongated)
        root.addChild("body", ModelPartBuilder.create()
            .uv(18, 4).cuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F) // main body
            .uv(52, 0).cuboid(-2.0F, 2.0F, -8.0F, 8.0F, 6.0F, 1.0F),    // modified udder: sizeX from 6F → 8F
            ModelTransform.of(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));

        // shared leg builder
        ModelPartBuilder leg = ModelPartBuilder.create()
            .uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);

        // legs
        root.addChild("right_hind_leg", leg, ModelTransform.pivot(-4.0F, 12.0F, 7.0F));
        root.addChild("left_hind_leg", leg, ModelTransform.pivot(4.0F, 12.0F, 7.0F));
        root.addChild("right_front_leg", leg, ModelTransform.pivot(-4.0F, 12.0F, -6.0F));
        root.addChild("left_front_leg", leg, ModelTransform.pivot(4.0F, 12.0F, -6.0F));

        return TexturedModelData.of(modelData, 64, 32);
    }

}