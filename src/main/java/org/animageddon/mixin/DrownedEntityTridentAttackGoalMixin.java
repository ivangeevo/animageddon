package org.animageddon.mixin;

import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(DrownedEntity.TridentAttackGoal.class)
public abstract class DrownedEntityTridentAttackGoalMixin extends ProjectileAttackGoal {


    //@Shadow @Final private DrownedEntity drowned;

    public DrownedEntityTridentAttackGoalMixin(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
        super(mob, mobSpeed, intervalTicks, maxShootRange);
    }


   // @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    private void injectedCanStart(CallbackInfoReturnable<Boolean> cir) {
       // cir.setReturnValue(super.canStart() && this.drowned.getMainHandStack().isOf(BTWR_Items.TRIDENT));
    }
}