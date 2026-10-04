package org.btwr.animageddon.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import org.btwr.animageddon.util.KickPose;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Cow and mooshroom. Other quadrupeds have no KickState, so it is a no-op for them. */
@Mixin(QuadrupedEntityModel.class)
public abstract class QuadrupedEntityModelMixin {
    @Shadow @Final protected ModelPart rightHindLeg;
    @Shadow @Final protected ModelPart leftHindLeg;

    @Inject(method = "setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At("TAIL"))
    private void kicking$pose(Entity entity, float limbAngle, float limbDistance, float animationProgress,
                              float headYaw, float headPitch, CallbackInfo ci) {
        KickPose.apply(entity, leftHindLeg, rightHindLeg, animationProgress, false);
    }
}