package org.ivangeevo.animageddon.entity.ai.goal;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.EnumSet;

public class CobwebShootGoal<T extends HostileEntity> extends Goal {
    private final T actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;

    public CobwebShootGoal(T actor, double speed, int attackInterval, float range) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canStart() {
        return this.actor.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        return (this.canStart() || !this.actor.getNavigation().isIdle());
    }

    @Override
    public void start() {
        super.start();
        this.actor.setAttacking(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.actor.setAttacking(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        boolean bl2;
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }
        double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
        bl2 = this.targetSeeingTicker > 0;
        if (bl != bl2) {
            this.targetSeeingTicker = 0;
        }
        if (d > (double)this.squaredRange || this.targetSeeingTicker < 20) {
            this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
            this.combatTicks = -1;
        } else {
            this.actor.getNavigation().stop();
            ++this.combatTicks;
        }
        if (this.combatTicks >= 20) {
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                this.movingToLeft = !this.movingToLeft;
            }
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }
            this.combatTicks = 0;
        }
        if (this.combatTicks > -1) {
            if (d > (double)(this.squaredRange * 0.75f)) {
                this.backward = false;
            } else if (d < (double)(this.squaredRange * 0.25f)) {
                this.backward = true;
            }
            this.actor.getMoveControl().strafeTo(this.backward ? -0.5f : 0.5f, this.movingToLeft ? 0.5f : -0.5f);
            Entity entity = this.actor.getControllingVehicle();
            if (entity instanceof MobEntity mobEntity)
            {
                mobEntity.lookAtEntity(livingEntity, 30.0f, 30.0f);
            }
            this.actor.lookAtEntity(livingEntity, 30.0f, 30.0f);
        } else {
            this.actor.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
        }
        if (this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
            shootCobweb();
            this.cooldown = this.attackInterval;
        } else {
            this.cooldown--;
        }
    }

    private void shootCobweb()
    {
        World world = this.actor.getWorld();
        Vec3d startPos = this.actor.getEyePos();
        Vec3d lookVec = this.actor.getRotationVec(1.0F);
        Vec3d endPos = startPos.add(lookVec.multiply(this.squaredRange));
        BlockHitResult hitResult = world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this.actor));

        if (hitResult.getType() == HitResult.Type.BLOCK)
        {
            BlockPos blockPos = hitResult.getBlockPos().offset(hitResult.getSide());
            world.setBlockState(blockPos, Blocks.COBWEB.getDefaultState());
        }
    }
}
