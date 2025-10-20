package org.ivangeevo.animageddon.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.entity.interfaces.AnimalEntityAdded;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements AnimalEntityAdded {

    // Cow related variables
    private int milkCooldown = 0;
    private int milkResetTime = 24000;

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    // Makes chickens not able to eat chicken feed again if they have already been fed in this egg laying cycle
    // We make sure that only adult chickens can be affected by this as babies cannot lay eggs anyway
    @ModifyReturnValue(method = "canEat", at = @At("RETURN"))
    private boolean modifyCanEat(boolean original) {
        if ((AnimalEntity)(Object)this instanceof ChickenEntity chicken && !chicken.isBaby()) {
            return original && !chicken.animageddon$getHasBeenFed();
        }

        return original;
    }

    // Makes chickens not breed-able with other chickens
    @Inject(method = "canBreedWith", at = @At("HEAD"), cancellable = true)
    private void onLovePlayer(AnimalEntity other, CallbackInfoReturnable<Boolean> cir) {
        if ((AnimalEntity)(Object)this instanceof ChickenEntity) {
            cir.setReturnValue(false);
        }
    }

    //@Inject(method = "mobTick", at = @At("HEAD"))
    private void onMobTick(CallbackInfo ci) {
        if ((AnimalEntity)(Object)this instanceof CowEntity cow) {
            if (!this.getWorld().isClient) {
                CowEntityAdded added = (CowEntityAdded) cow;
                var attachments = cow.getAttachedOrCreate(ModDataAttachments.MILK_COOLDOWN);
                int cooldown = cow.getAttached(ModDataAttachments.MILK_COOLDOWN) != null
                        ? cow.getAttached(ModDataAttachments.MILK_COOLDOWN)
                        : 0;

                if (!added.gotMilk()) {
                    cooldown++;
                    if (cooldown >= 24000) {
                        added.setGotMilk(true);
                        cooldown = 0;
                    }
                    cow.setAttached(ModDataAttachments.MILK_COOLDOWN, cooldown);
                } else {
                    cow.setAttached(ModDataAttachments.MILK_COOLDOWN, 0);
                }
            }
        }
    }

}