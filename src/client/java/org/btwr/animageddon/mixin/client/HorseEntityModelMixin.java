package org.btwr.animageddon.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.btwr.animageddon.util.KickPose;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Horse, donkey, mule and undead horses (donkey/mule models call super.setAngles). */
@Mixin(HorseEntityModel.class)
public abstract class HorseEntityModelMixin {
    @Shadow @Final private ModelPart leftHindLeg;
    @Shadow @Final private ModelPart rightHindLeg;

    @Inject(method = "setAngles(Lnet/minecraft/entity/passive/AbstractHorseEntity;FFFFF)V", at = @At("TAIL"))
    private void kicking$pose(AbstractHorseEntity entity, float limbAngle, float limbDistance, float animationProgress,
                              float headYaw, float headPitch, CallbackInfo ci) {
        KickPose.apply(entity, leftHindLeg, rightHindLeg, animationProgress, true); // horses buck with both legs
    }
}