package org.btwr.animageddon.mixin.entity.ai;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WanderAroundGoal.class)
public abstract class WanderAroundGoalMixin {

    @Inject(method = "canStart", at = @At("HEAD"))
    private void resetDespawnCounterForWandering(CallbackInfoReturnable<Boolean> cir) {
        WanderAroundGoal self = (WanderAroundGoal)(Object)this;
        MobEntity mob = ((WanderAroundGoalAccessor) self).getMob();

        if (mob instanceof Monster && !mob.isAiDisabled() && mob.getWorld().isNight()) {
            // Reset despawn counter at night, so mobs don't despawn being far away
            // from the player and also keep on wandering around (bypasses "too far" limitation)
            mob.setDespawnCounter(0);
        }
    }

}