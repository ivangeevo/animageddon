package org.ivangeevo.animageddon.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity {

    @Shadow protected abstract boolean getHorseFlag(int bitmask);

    @Shadow protected abstract void setHorseFlag(int bitmask, boolean flag);

    @Unique private int fedLevel = 0;

    @Unique protected final int maxFedLevel = 3000;

    @Unique private final int rideableFedLevel = 600;

    @Unique private final int untamedFedLevel = maxFedLevel / 8; // 400


    // holds the value of whether the horse is fed enough to be ridden
    @Unique private static final int FED_FLAG = 128;


    @Unique public boolean isFed() {
        return this.getHorseFlag(FED_FLAG);
    }

    @Unique
    public void setFed(boolean fed) {
        this.setHorseFlag(FED_FLAG, fed);
    }

    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    //@Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        --fedLevel;
        if (fedLevel < rideableFedLevel) {
            this.stopRiding();
        }
    }

    //@Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        --fedLevel;
        --fedLevel;
    }

    //@Inject(method = "receiveFood", at = @At(value = "INVOKE", target= "Lnet/minecraft/entity/passive/AbstractHorseEntity;isBaby()Z"))
    private void onReceiveFood$beforeHeal(PlayerEntity player, ItemStack item, CallbackInfoReturnable<Boolean> cir, @Local float f) {
        if (fedLevel < maxFedLevel) {
            fedLevel += (int) f;
        } else {
            setFed(true);
        }
    }


}
