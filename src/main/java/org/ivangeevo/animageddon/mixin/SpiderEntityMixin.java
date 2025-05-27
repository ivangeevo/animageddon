package org.ivangeevo.animageddon.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.interfaces.SpiderEntityAdded;
import org.ivangeevo.animageddon.entity.projectile.CobwebEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpiderEntity.class)
public abstract class SpiderEntityMixin extends HostileEntity implements SpiderEntityAdded {

    @Unique
    private static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Integer> TIME_TO_NEXT_WEB = DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Unique
    private static final int TIME_BETWEEN_WEBS = 20 * 60 * 20; // 1 minute in ticks

    protected SpiderEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void injectedInitGoals(CallbackInfo ci) {
        this.targetSelector.add(4, new SpiderEntity.TargetGoal<>((SpiderEntity) (Object) this, ChickenEntity.class));
        this.targetSelector.add(4, new SpiderEntity.TargetGoal<>((SpiderEntity) (Object) this, RabbitEntity.class));
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(SHOOTING, false);
        builder.add(TIME_TO_NEXT_WEB, 0);
    }

    @Override public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    @Override public int getTimeToNextWeb() {
        return this.dataTracker.get(TIME_TO_NEXT_WEB);
    }

    @Override public void setTimeToNextWeb(int timeToNextWeb) {
        this.dataTracker.set(TIME_TO_NEXT_WEB, timeToNextWeb);
    }

    @Override public boolean hasWeb() { return this.dataTracker.get(TIME_TO_NEXT_WEB) <= 0; }

    @Override
    public void spitWeb(Entity targetEntity) {

        if (!getWorld().isClient()) {
            if (this.getTimeToNextWeb() <= 0) {
                Vec3d vec3d = this.getRotationVec(1.0F);
                double f = targetEntity.getX() - (this.getX() + vec3d.x * 4.0);
                double g = targetEntity.getBodyY(0.5) - (0.5 + this.getBodyY(0.5));
                double h = targetEntity.getZ() - (this.getZ() + vec3d.z * 4.0);
                CobwebEntity cobwebEntity = new CobwebEntity(getWorld(), this, f, g, h);
                getWorld().spawnEntity(cobwebEntity);
                this.setTimeToNextWeb(TIME_BETWEEN_WEBS); // Set cooldown
            }
        }
    }
}

