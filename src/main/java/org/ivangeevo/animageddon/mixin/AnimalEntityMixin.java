package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.interfaces.AnimalEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements AnimalEntityAdded {

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    // Makes chickens not breed-able with other chickens
    @Inject(method = "canBreedWith", at = @At("HEAD"), cancellable = true)
    private void onLovePlayer(AnimalEntity other, CallbackInfoReturnable<Boolean> cir) {
        if ((AnimalEntity)(Object)this instanceof ChickenEntity) {
            cir.setReturnValue(false);
        }
    }

}