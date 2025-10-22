package org.ivangeevo.animageddon.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.data.ChickenEggAttachedData;
import org.ivangeevo.animageddon.data.CowMilkAttachedData;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.entity.interfaces.AnimalEntityAdded;
import org.ivangeevo.animageddon.util.ServerTimeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements AnimalEntityAdded {

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    // Prevents chickens from eating chicken feed again within the same egg-laying cycle.
    // Only affects adult chickens since babies cannot lay eggs.
    @ModifyReturnValue(method = "canEat", at = @At("RETURN"))
    private boolean modifyCanEat(boolean original) {
        if (!((AnimalEntity)(Object) this instanceof ChickenEntity chicken) || chicken.isBaby()) {
            return original;
        }

        ChickenEggAttachedData data = chicken.getAttachedOrCreate(
                ModDataAttachments.CHICKEN_EGG_DATA,
                () -> ChickenEggAttachedData.DEFAULT
        );
        if (data == null) return original;

        return original && !data.getHasBeenFed();
    }


    // Makes chickens not breed-able with other chickens
    @Inject(method = "canBreedWith", at = @At("HEAD"), cancellable = true)
    private void onLovePlayer(AnimalEntity other, CallbackInfoReturnable<Boolean> cir) {
        if ((AnimalEntity)(Object)this instanceof ChickenEntity) {
            cir.setReturnValue(false);
        }
    }

    // Tick animal attached data
    @Inject(method = "mobTick", at = @At("HEAD"))
    private void onMobTick(CallbackInfo ci) {
        if ((AnimalEntity)(Object)this instanceof CowEntity cow && !cow.isBaby()) {
            if (!cow.getWorld().isClient) {
                CowMilkAttachedData data = cow.getAttachedOrCreate(
                        ModDataAttachments.MILK_DATA,
                        () -> CowMilkAttachedData.DEFAULT
                );
                data.tick();
                cow.setAttached(ModDataAttachments.MILK_DATA, data);
            }
        }

        if ((AnimalEntity)(Object)this instanceof ChickenEntity chicken && !chicken.isBaby()) {
            if (!chicken.getWorld().isClient) {
                ChickenEggAttachedData data = chicken.getAttachedOrCreate(
                        ModDataAttachments.CHICKEN_EGG_DATA,
                        () -> ChickenEggAttachedData.DEFAULT
                );
                data.tick(chicken);
                chicken.setAttached(ModDataAttachments.CHICKEN_EGG_DATA, data);
            }
        }
    }

    @Inject(method = "eat", at = @At("HEAD"))
    private void onEatChicken(PlayerEntity player, Hand hand, ItemStack stack, CallbackInfo ci) {
        // allow feeding only of adult chickens
        if ((AnimalEntity)(Object)this instanceof ChickenEntity chicken && !chicken.isBaby()) {

            long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();
            ChickenEggAttachedData data = chicken.getAttachedOrCreate(
                    ModDataAttachments.CHICKEN_EGG_DATA, () -> ChickenEggAttachedData.DEFAULT
            );

            // don't try to feed if already fed
            if (data != null && !data.getHasBeenFed()) {

                if (stack.isIn(ItemTags.CHICKEN_FOOD)) {
                    // the following morning, at least half a day from now
                    long timeToLayEgg = (((currentTime + 12000L) / 24000L) + 1) * 24000L;

                    // crack of dawn (22550) + 30-second random variance
                    timeToLayEgg += -1450 + chicken.getRandom().nextInt(600);

                    chicken.playSound(SoundEvents.ENTITY_CHICKEN_HURT, 1.0F, chicken.getRandom().nextFloat() * 0.2F + 1.5F);

                    data.setTimeToLayEgg(timeToLayEgg);
                    data.setHasBeenFed(true);
                    chicken.setAttached(ModDataAttachments.CHICKEN_EGG_DATA, data);
                }
            }
        }
    }

    //TODO: [BUG] Cow baby unexpected behavior

    // Tried adding a special case for baby cows to be able to eat breeding items to grow up
    // Not sure if this is a good idea actually because the breeding item is cake
    //@Inject(method = "eat", at = @At("HEAD"))
    private void onEatCow(PlayerEntity player, Hand hand, ItemStack stack, CallbackInfo ci) {
        // allow feeding only of adult cows
        if ((AnimalEntity)(Object)this instanceof CowEntity cow && !cow.isBaby()) {

            long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();
            ChickenEggAttachedData data = cow.getAttachedOrCreate(
                    ModDataAttachments.CHICKEN_EGG_DATA, () -> ChickenEggAttachedData.DEFAULT
            );

            // don't try to feed if already fed
            if (data != null && !data.getHasBeenFed()) {

                if (stack.isIn(ItemTags.COW_FOOD)) {
                }
            }
        }
    }

    //@Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;eat(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"), cancellable = true)
    private void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {

        // allow feeding only of adult chickens
        if ((AnimalEntity)(Object)this instanceof ChickenEntity chicken && !chicken.isBaby()) {

            long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();
            ChickenEggAttachedData data = chicken.getAttachedOrCreate(
                    ModDataAttachments.CHICKEN_EGG_DATA, () -> ChickenEggAttachedData.DEFAULT
            );

            // don't try to feed if already fed
            if (data != null && !data.getHasBeenFed()) {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.isIn(ItemTags.CHICKEN_FOOD)) {
                    // the following morning, at least half a day from now
                    long timeToLayEgg = (((currentTime + 12000L) / 24000L) + 1) * 24000L;

                    // crack of dawn (22550) + 30-second random variance
                    timeToLayEgg += -1450 + chicken.getRandom().nextInt(600);

                    chicken.playSound(SoundEvents.ENTITY_CHICKEN_HURT, 1.0F, chicken.getRandom().nextFloat() * 0.2F + 1.5F);

                    data.setTimeToLayEgg(timeToLayEgg);
                    data.setHasBeenFed(true);
                    chicken.setAttached(ModDataAttachments.CHICKEN_EGG_DATA, data);
                }
            }
        }
    }

}