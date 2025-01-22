package org.ivangeevo.animageddon.ai.goal;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class FleeBlockPlacementGoal extends Goal {
    private final PathAwareEntity mob;
    private final double speed;
    private final int fleeRadius;
    private double targetX;
    private double targetY;
    private double targetZ;

    public FleeBlockPlacementGoal(PathAwareEntity mob, double speed, int fleeRadius) {
        this.mob = mob;
        this.speed = speed;
        this.fleeRadius = fleeRadius;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (isBlockPlacedNearby()) {
            Vec3d fleeTarget = findFleeTarget();
            if (fleeTarget != null) {
                this.targetX = fleeTarget.x;
                this.targetY = fleeTarget.y;
                this.targetZ = fleeTarget.z;
                return true;
            }
        }
        return false;
    }

    private boolean isBlockPlacedNearby() {
        World world = mob.getWorld();
        BlockPos mobPos = mob.getBlockPos();

        for (BlockPos pos : BlockPos.iterateOutwards(mobPos, fleeRadius, fleeRadius, fleeRadius)) {
            if (!world.getBlockState(pos).isAir()) { // Checks for non-air blocks
                return true;
            }
        }

        return false;
    }

    private Vec3d findFleeTarget() {
        return NoPenaltyTargeting.find(mob, fleeRadius, 4); // Finds a flee path within the given radius
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Override
    public void stop() {
        this.targetX = 0;
        this.targetY = 0;
        this.targetZ = 0;
    }
}
