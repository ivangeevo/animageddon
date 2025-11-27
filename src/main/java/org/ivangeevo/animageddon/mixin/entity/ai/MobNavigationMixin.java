package org.ivangeevo.animageddon.mixin.entity.ai;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MobNavigation.class)
public abstract class MobNavigationMixin extends EntityNavigation {

    public MobNavigationMixin(MobEntity entity, World world) {
        super(entity, world);
    }

    //@Inject(method = "findPathTo(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/entity/ai/pathing/Path;", at = @At("RETURN"), cancellable = true)
    private void restrictBabyPath(BlockPos target, int distance, CallbackInfoReturnable<Path> cir) {
        if (!(this.entity instanceof AnimalEntity animal) || !animal.isBaby()) return;

        Path path = cir.getReturnValue();
        if (path == null) return;

        List<PathNode> newNodes = new ArrayList<>();
        for (int i = 0; i < path.getLength(); i++) {
            PathNode node = path.getNode(i);
            double stepHeight = node.y - (i == 0 ? this.entity.getBlockY() : path.getNode(i - 1).y);
            if (stepHeight <= 0.5) { // baby can step
                newNodes.add(node);
            }
        }

        if (newNodes.isEmpty()) {
            cir.setReturnValue(null); // no path reachable
        } else {
            Path newPath = new Path(newNodes, path.getTarget(), path.reachesTarget());
            cir.setReturnValue(newPath);
        }
    }

}