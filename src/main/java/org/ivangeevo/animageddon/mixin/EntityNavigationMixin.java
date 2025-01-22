package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityNavigation.class)
public abstract class EntityNavigationMixin {
    @Shadow @Final protected MobEntity entity;

    @Shadow @Nullable protected Path currentPath;

    @Shadow @Final protected World world;

    @Shadow @Nullable private BlockPos currentTarget;

    @Inject(method = "canJumpToNext", at = @At("HEAD"), cancellable = true)
    private void preventBabyJumps(PathNodeType nodeType, CallbackInfoReturnable<Boolean> cir) {
        if (this.entity instanceof AnimalEntity animal && animal.isBaby()) {

            if (currentTarget == null) {
                return;
            }

            BlockPos nextBlock = new BlockPos(currentTarget.getX(), currentTarget.getY() - 1, currentTarget.getZ());
            if (world.getBlockState(nextBlock).isSolidBlock(world, nextBlock)) {
                double blockHeight = world.getBlockState(nextBlock).getOutlineShape(world, nextBlock).getBoundingBox().maxY;
                if (blockHeight > 0.5) {
                    cir.setReturnValue(false);
                }
            }

        }
    }

    @Inject(method = "continueFollowingPath", at = @At("HEAD"), cancellable = true)
    private void restrictBabyJumping(CallbackInfo ci) {
        if (this.entity instanceof AnimalEntity animal && animal.isBaby()) {
            Vec3i nextNodePos = this.currentPath.getCurrentNodePos();
            BlockPos blockPos = new BlockPos(nextNodePos);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                double height = world.getBlockState(blockPos).getOutlineShape(world, blockPos).getBoundingBox().maxY;
                if (height > 0.5) { // Limit to slab or smaller
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "shouldJumpToNextNode", at = @At("HEAD"), cancellable = true)
    private void restrictBabyJumpNodes(Vec3d currentPos, CallbackInfoReturnable<Boolean> cir) {
        if (this.entity instanceof AnimalEntity animal && animal.isBaby()) {
            // Prevent jump to high blocks here, similar to the logic above.
            cir.setReturnValue(false);
        }
    }


}

