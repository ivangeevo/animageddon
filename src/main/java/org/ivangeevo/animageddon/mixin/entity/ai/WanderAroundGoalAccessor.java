package org.ivangeevo.animageddon.mixin.entity.ai;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WanderAroundGoal.class)
public interface WanderAroundGoalAccessor {

    @Accessor PathAwareEntity getMob();

}