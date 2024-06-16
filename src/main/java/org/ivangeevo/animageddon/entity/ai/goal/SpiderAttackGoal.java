package org.ivangeevo.animageddon.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SpiderEntity;
import org.ivangeevo.animageddon.entity.interfaces.SpiderEntityAdded;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class SpiderAttackGoal extends Goal {
    private final MobEntity actor;
    private LivingEntity target;
    private int cooldown;

    public SpiderAttackGoal(MobEntity actor) {
        this.actor = actor;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return false;
        }
        this.target = livingEntity;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (!this.target.isAlive()) {
            return false;
        }
        if (this.actor.squaredDistanceTo(this.target) > 225.0) {
            return false;
        }
        return !this.actor.getNavigation().isIdle() || this.canStart();
    }

    @Override
    public void stop() {
        this.target = null;
        this.actor.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.actor.getLookControl().lookAt(this.target, 30.0f, 30.0f);
        double d = this.actor.getWidth() * 2.0f * (this.actor.getWidth() * 2.0f);
        double e = this.actor.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
        double f = 0.8;
        if (e > d && e < 16.0) {
            f = 1.33;
        } else if (e < 225.0) {
            f = 0.6;
        }
        this.actor.getNavigation().startMovingTo(this.target, f);
        this.cooldown = Math.max(this.cooldown - 1, 0);
        if (e > d) {
            return;
        }
        if (this.cooldown > 0) {
            return;
        }
        if (this.actor instanceof SpiderEntity spider && ((SpiderEntityAdded)spider).hasWeb()) {
            ((SpiderEntityAdded)spider).spitWeb(this.target);
            this.cooldown = 20;
        } else {
            this.cooldown = 20;
            this.actor.tryAttack(this.target);
        }
    }
}
