package org.ivangeevo.animageddon.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.interfaces.SpiderEntityAdded;

public class ShootFireballGoal extends Goal
{
    private final SpiderEntity spider;
    public int cooldown;



    public ShootFireballGoal(SpiderEntity spider) {
        this.spider = spider;
    }

    public boolean canStart() {
        return this.spider.getTarget() != null;
    }

    public void start() {
        this.cooldown = 0;
    }

    public void stop() {
        ((SpiderEntityAdded)this.spider).setShooting(false);
    }


    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.spider.getTarget();
        if (livingEntity != null) {
            double d = 64.0;
            if (livingEntity.squaredDistanceTo(this.spider) < 4096.0 && this.spider.canSee(livingEntity)) {
                World world = this.spider.getWorld();
                ++this.cooldown;
                if (this.cooldown == 10 && !this.spider.isSilent()) {
                    world.syncWorldEvent((PlayerEntity)null, 1015, this.spider.getBlockPos(), 0);
                }

                if (this.cooldown == 20) {
                    double e = 4.0;
                    Vec3d vec3d = this.spider.getRotationVec(1.0F);
                    double f = livingEntity.getX() - (this.spider.getX() + vec3d.x * 4.0);
                    double g = livingEntity.getBodyY(0.5) - (0.5 + this.spider.getBodyY(0.5));
                    double h = livingEntity.getZ() - (this.spider.getZ() + vec3d.z * 4.0);
                    if (!this.spider.isSilent()) {
                        world.syncWorldEvent((PlayerEntity)null, 1016, this.spider.getBlockPos(), 0);
                    }

                    FireballEntity fireballEntity = new FireballEntity(world, this.spider, f, g, h, 1);
                    fireballEntity.setPosition(this.spider.getX() + vec3d.x * 4.0, this.spider.getBodyY(0.5) + 0.5, fireballEntity.getZ() + vec3d.z * 4.0);
                    world.spawnEntity(fireballEntity);
                    this.cooldown = -40;
                }
            } else if (this.cooldown > 0) {
                --this.cooldown;
            }

            ((SpiderEntityAdded) this.spider).setShooting(this.cooldown > 10);
        }
    }
}