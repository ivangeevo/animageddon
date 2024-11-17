package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.ivangeevo.animageddon.entity.interfaces.AnimalEntityAdded.FULL_HUNGER_COUNT;


@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity implements CowEntityAdded
{
    private int hungerCountdown = FULL_HUNGER_COUNT;

    @Unique private final PassiveEntity thisEntity = (PassiveEntity)(Object)this;

    protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    // Set custom data for CowEntity
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onInitDataTracker(DataTracker.Builder builder, CallbackInfo ci)
    {
        if (thisEntity instanceof CowEntity)
        {
            builder.add(GOT_MILK, false);
            builder.add(WEARING_BREEDING_HARNESS, false);
        }
    }

}
