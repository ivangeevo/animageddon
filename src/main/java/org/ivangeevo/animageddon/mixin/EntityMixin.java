package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.ivangeevo.animageddon.entity.interfaces.EntityAdded;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAdded
{

    @Shadow public abstract @Nullable Entity getVehicle();

    @Shadow public abstract boolean startRiding(Entity entity);

    @Shadow private Vec3d velocity;

    @Shadow public abstract double getX();

    @Shadow public abstract double getZ();

    @Shadow
    private boolean onGround;

    @Shadow @Final protected Random random;

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Override
    public void onKickedByCow(CowEntity cow) {
        flingAwayFromEntity(cow, getCowKickMovementMultiplier());
    }

    protected double getCowKickMovementMultiplier()
    {
        return 1D;
    }


    public void flingAwayFromEntity(Entity repulsingEntity, double dForceMultiplier) {
        if (getVehicle() != null) {
            startRiding(null);
        }

        double dVelocityX = velocity.x;
        double dVelocityZ = velocity.z;

        double dDeltaX = getX() - repulsingEntity.getX();
        double dDeltaZ = getZ() - repulsingEntity.getZ();

        double dFlatDistToTargetSq = dDeltaX * dDeltaX + dDeltaZ * dDeltaZ;

        if (dFlatDistToTargetSq > 0.1D) {
            double dFlatDistToTarget = MathHelper.square(dFlatDistToTargetSq);

            dVelocityX += (dDeltaX / dFlatDistToTarget) * 0.5D * dForceMultiplier;
            dVelocityZ += (dDeltaZ / dFlatDistToTarget) * 0.5F * dForceMultiplier;
        }

        // Instead of setting isAirBorne, use the onGround property
        // If onGround is false, the entity is considered airborne
        onGround = false;

        double dVelocityY = velocity.getY() + (0.25D * dForceMultiplier);

        dVelocityX *= (random.nextDouble() * 0.2D) + 0.9;
        dVelocityZ *= (random.nextDouble() * 0.2D) + 0.9;

        this.setVelocity(new Vec3d(
                MathHelper.clamp(dVelocityX, -1.0D, 1.0D),
                MathHelper.clamp(dVelocityY, 0.0D, 0.75D),
                MathHelper.clamp(dVelocityZ, -1.0D, 1.0D)
        ));
    }


}
