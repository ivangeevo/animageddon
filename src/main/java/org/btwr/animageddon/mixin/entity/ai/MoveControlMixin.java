package org.btwr.animageddon.mixin.entity.ai;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoveControl.class)
public abstract class MoveControlMixin {

    @Final @Shadow protected MobEntity entity;

    //@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/control/JumpControl;setActive()V"), cancellable = true)
    private void preventBabyAnimalJump(CallbackInfo ci) {
        if (this.entity instanceof AnimalEntity animal && animal.isBaby()) {
            // cancel the jump activation for babies
            ci.cancel();
        }
    }

}