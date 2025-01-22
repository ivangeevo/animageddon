package org.ivangeevo.animageddon.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class ScareAnimalOnBlockPlacementGoal extends Goal {
    protected final AnimalEntity animal;
    protected final double searchRadius;
    protected final double fleeSpeed;
    private long lastCheckTime;

    public ScareAnimalOnBlockPlacementGoal(AnimalEntity animal, double searchRadius, double fleeSpeed) {
        this.animal = animal;
        this.searchRadius = searchRadius;
        this.fleeSpeed = fleeSpeed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!(animal.getWorld() instanceof ServerWorld world)) {
            return false;
        }

        long currentTime = world.getTime();
        if (currentTime == lastCheckTime) {
            return false; // Prevent checking multiple times within the same tick
        }

        lastCheckTime = currentTime;

        BlockPos animalPos = animal.getBlockPos();
        for (BlockPos pos : BlockPos.iterateOutwards(animalPos, (int) searchRadius, (int) searchRadius, (int) searchRadius)) {
            if (world.getBlockState(pos).getBlock() != null && !world.isAir(pos)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        if (!animal.canMoveVoluntarily()) {
            return;
        }

        BlockPos animalPos = animal.getBlockPos();
        double fleeX = animal.getX() + (animal.getRandom().nextDouble() - 0.5) * 2 * searchRadius;
        double fleeZ = animal.getZ() + (animal.getRandom().nextDouble() - 0.5) * 2 * searchRadius;

        animal.getNavigation().startMovingTo(fleeX, animal.getY(), fleeZ, fleeSpeed);
    }

    @Override
    public boolean shouldContinue() {
        return animal.getNavigation().isFollowingPath();
    }
}
