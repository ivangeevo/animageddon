package org.ivangeevo.animageddon.entity.ai.goal;

import net.minecraft.entity.LivingEntity;

public interface CustomZombieGoals {
    boolean isTargetingAnimals();

    void setTargetingAnimals(boolean targetingAnimals);

    LivingEntity getTargetEntity();

    void setTargetEntity(LivingEntity targetEntity);
}
