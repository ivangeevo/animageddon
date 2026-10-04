package org.btwr.animageddon.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.Vec3d;
import org.btwr.animageddon.entity.animal_kicking.KickLogic;

import java.util.EnumSet;

public class FleeFromAttackerGoal extends Goal {
    private final AnimalEntity animal;
    private final double speed;
    private double targetX, targetY, targetZ;

    public FleeFromAttackerGoal(AnimalEntity animal, double speed) {
        this.animal = animal;
        this.speed = speed;
        setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        Vec3d target = null;

        if (animal.isOnFire()) {
            target = FuzzyTargeting.find(animal, 5, 4); // random spot, panicking
        } else {
            LivingEntity threat = KickLogic.getThreat(animal);
            if (threat != null) {
                target = NoPenaltyTargeting.findFrom(animal, 5, 4, threat.getPos());
            }
        }

        if (target == null) return false;
        targetX = target.x;
        targetY = target.y;
        targetZ = target.z;
        return true;
    }

    @Override
    public void start() {
        animal.getNavigation().startMovingTo(targetX, targetY, targetZ, speed);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity threat = KickLogic.getThreat(animal);

        if (animal.getNavigation().isIdle() || (threat == null && !animal.isOnFire())) {
            return false;
        }

        if (animal.isLeashed()) {
            animal.detachLeash(true, true); // BTW: breakLeash()
        }

        if (threat == null) {
            return true; // burning and running around in panic
        }

        // Choose another spot once we are close to the current one.
        if (animal.squaredDistanceTo(targetX, targetY, targetZ) > 4.0) {
            // Keep going only while the destination is farther from the attacker than we are,
            // which stops animals circling after they overshoot.
            return animal.squaredDistanceTo(threat) < threat.squaredDistanceTo(targetX, targetY, targetZ);
        }
        return false;
    }
}