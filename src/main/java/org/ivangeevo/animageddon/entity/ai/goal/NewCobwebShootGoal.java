package org.ivangeevo.animageddon.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import org.ivangeevo.animageddon.data.ModDataAttachments;

import java.util.EnumSet;

public class NewCobwebShootGoal extends Goal {

    final SpiderEntity spider;
    private int websFired;
    int cooldown;
    private int targetNotVisibleTicks;

    public NewCobwebShootGoal(SpiderEntity spider) {
        this.spider = spider;
        this.setControls(EnumSet.noneOf(Control.class));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.spider.getTarget();
        return livingEntity != null && livingEntity.isAlive() && this.spider.canTarget(livingEntity);
    }

    @Override
    public void start() {
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        var webData = spider.getAttached(ModDataAttachments.SPIDER_WEB_DATA);
        if (webData != null) {
            webData.setShooting(false);
        }
        this.targetNotVisibleTicks = 0;

    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        // New
        this.cooldown--;
        LivingEntity livingEntity = this.spider.getTarget();

        var webData = spider.getAttached(ModDataAttachments.SPIDER_WEB_DATA);
        if (webData == null) return;

        if (livingEntity != null) {
            boolean bl = this.spider.getVisibilityCache().canSee(livingEntity);
            if (bl) {
                this.targetNotVisibleTicks = 0;
            } else {
                this.targetNotVisibleTicks++;
            }

            double d = this.spider.squaredDistanceTo(livingEntity);
            if (d < 4.0) {
                if (!bl) {
                    return;
                }

                if (this.cooldown <= 0) {
                    this.cooldown = 20;
                    this.spider.tryAttack(livingEntity);
                }

                this.spider.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0);
            } else if (d < 32 * 32 && bl) {
                double e = livingEntity.getX() - this.spider.getX();
                double f = livingEntity.getBodyY(0.5) - this.spider.getBodyY(0.5);
                double g = livingEntity.getZ() - this.spider.getZ();
                if (this.cooldown <= 0) {
                    if (this.websFired == 1) {
                        this.cooldown = 60;
                        webData.setShooting(true);
                    }
                    else if (this.websFired <= 4) {
                        this.cooldown = 6;
                    }
                    else {
                        this.cooldown = 100;
                        this.websFired = 0;
                        webData.setShooting(false);
                    }

                    if (this.websFired > 1) {
                        double h = Math.sqrt(Math.sqrt(d)) * 0.5;
                        if (!this.spider.isSilent()) {
                            this.spider.getWorld().syncWorldEvent(null, WorldEvents.BLAZE_SHOOTS, this.spider.getBlockPos(), 0);
                        }

                        for (int i = 0; i < 1; i++) {
                            Vec3d vec3d = new Vec3d(this.spider.getRandom().nextTriangular(e, 2.297 * h), f, this.spider.getRandom().nextTriangular(g, 2.297 * h));
                            SmallFireballEntity smallFireballEntity = new SmallFireballEntity(this.spider.getWorld(), this.spider, vec3d.normalize());
                            smallFireballEntity.setPosition(smallFireballEntity.getX(), this.spider.getBodyY(0.5) + 0.5, smallFireballEntity.getZ());
                            this.spider.getWorld().spawnEntity(smallFireballEntity);
                        }
                    }
                }

                this.spider.getLookControl().lookAt(livingEntity, 10.0F, 10.0F);
            }
            else if (this.targetNotVisibleTicks < 5) {
                this.spider.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0);
            }

            super.tick();
        }
    }

}