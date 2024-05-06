package org.ivangeevo.animageddon.entity.ai.goal;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class CustomWanderAroundGoal extends WanderAroundGoal {
    public CustomWanderAroundGoal(PathAwareEntity mob, double speed) {
        super(mob, speed);
    }


    @Override
    public void start() {
        Vec3d playerPos = this.mob.getPos();
        double angle = this.mob.getRandom().nextDouble() * 2.0 * Math.PI;
        double distance = 25.0 + this.mob.getRandom().nextDouble() * 5.0; // Adjusted distance
        double targetX = playerPos.getX() + distance * Math.cos(angle);
        double targetY = playerPos.getY();
        double targetZ = playerPos.getZ() + distance * Math.sin(angle);

        // Start moving towards the calculated position
        this.mob.getNavigation().startMovingTo(targetX, targetY, targetZ, this.speed);
    }
}