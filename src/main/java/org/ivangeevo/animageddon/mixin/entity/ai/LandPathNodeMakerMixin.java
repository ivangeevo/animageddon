package org.ivangeevo.animageddon.mixin.entity.ai;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathContext;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LandPathNodeMaker.class)
public abstract class LandPathNodeMakerMixin extends PathNodeMaker {

    //@Inject(method = "getDefaultNodeType", at = @At("HEAD"), cancellable = true)
    private void restrictBabyStep(PathContext context, int x, int y, int z, CallbackInfoReturnable<PathNodeType> cir) {
        if (entity instanceof AnimalEntity animal && animal.isBaby()) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockPos below = pos.down();

            // Compute step height from collision shapes
            double stepHeight = entity.getWorld().getBlockState(pos).getCollisionShape(entity.getWorld(), pos).getMax(Direction.Axis.Y)
                    - entity.getWorld().getBlockState(below).getCollisionShape(entity.getWorld(), below).getMax(Direction.Axis.Y);

            // Babies can only step <= 0.5 blocks
            if (stepHeight > 0.5) {
                cir.setReturnValue(PathNodeType.BLOCKED);
            }
        }
    }

}