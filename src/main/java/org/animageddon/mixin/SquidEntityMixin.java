package org.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.animageddon.entity.interfaces.SquidEntityAdded;

@Mixin(SquidEntity.class)
public abstract class SquidEntityMixin extends WaterCreatureEntity implements SquidEntityAdded
{

    private static final int TENTACLE_ATTACK_TICKS_TO_COOLDOWN = 100;

    private int tentacleAttackCooldownTimer = TENTACLE_ATTACK_TICKS_TO_COOLDOWN;

    private int tentacleAttackInProgressCounter  = -1;

    @Override
    public int tentacleAttackInProgressCounter() {
        return tentacleAttackInProgressCounter;
    }

    @Override
    public void setTentacleAttackInProgressCounter(int value) {
        tentacleAttackInProgressCounter = value;
    }


    protected SquidEntityMixin(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
    }


    private double tentacleAttackTargetX = 0D;
    private double tentacleAttackTargetY = 0D;
    private double tentacleAttackTargetZ = 0D;


    @Override
    public void onClientNotifiedOfTentacleAttack(double dTargetX, double dTargetY, double dTargetZ) {
        tentacleAttackInProgressCounter = 0;

        tentacleAttackTargetX = dTargetX;
        tentacleAttackTargetY = dTargetY;
        tentacleAttackTargetZ = dTargetZ;

        getWorld().playSound( null, this.getBlockPos(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.HOSTILE, 1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
        getWorld().playSound(null,  this.getBlockPos(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);

        if ( isTouchingWater() )
        {
            for ( int iParticleCount = 0; iParticleCount < 150; iParticleCount++ )
            {
                this.getWorld().addParticle(ParticleTypes.BUBBLE,
                        getX() + ( random.nextDouble() * 2F ) - 1F,
                        getY() + random.nextDouble(),
                        getZ() + ( random.nextDouble() * 2F ) - 1F,
                        0D, 0D, 0D);
            }

            for ( int iParticleCount = 0; iParticleCount < 10; iParticleCount++ )
            {
                this.getWorld().addParticle( ParticleTypes.SPLASH,
                        getX() + ( random.nextDouble() * 2F ) - 1F,
                        getY() + getHeight(),
                        getZ() + ( random.nextDouble() * 2F ) - 1F,
                        0D, 0D, 0D);
            }

            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.HOSTILE, 1F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }

    }

    @Override
    public void onKickedByCow(CowEntity cow) {

    }


}
