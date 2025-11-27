package org.btwr.animageddon.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.btwr.animageddon.data.attachments.ChickenEggAttachedData;
import org.btwr.animageddon.data.attachments.CowMilkAttachedData;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.util.ServerTimeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {

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

        var data = chicken.getAttached(ModDataAttachments.CHICKEN_EGG_DATA);
        if (data == null) return original;

        return original && !data.getHasBeenFed();
    }

    // Makes chickens not breed-able with other chickens
    @Inject(method = "canBreedWith", at = @At("HEAD"), cancellable = true)
    private void onLovePlayer(AnimalEntity other, CallbackInfoReturnable<Boolean> cir) {
        forAnimalSubclass(ChickenEntity.class, chicken -> cir.setReturnValue(false));
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(EntityType<?> entityType, World world, CallbackInfo ci) {

        /**
        forAnimalSubclass(CowEntity.class, animal -> {
            var hungerData = animal.getAttachedOrCreate(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA, LivingEntityHungerData::forDefault);
            animal.setAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA, hungerData);
        });
         **/


        // Chicken attached data
        forAnimalSubclass(ChickenEntity.class, chicken -> {
            var eggData = chicken.getAttachedOrCreate(ModDataAttachments.CHICKEN_EGG_DATA, ChickenEggAttachedData::forDefault);
            chicken.setAttached(ModDataAttachments.CHICKEN_EGG_DATA, eggData);
        });

        // Cow attached data
        forAnimalSubclass(CowEntity.class, cow -> {
            var milkData = cow.getAttachedOrCreate(ModDataAttachments.MILK_DATA, CowMilkAttachedData::forDefault);
            cow.setAttached(ModDataAttachments.MILK_DATA, milkData);
        });
    }

    //@Inject(method = "mobTick", at = @At("HEAD"))
    private void onMobTick1(CallbackInfo ci) {
        forAnimalSubclass(CowEntity.class, this::animalServerTick);

    }

    private void animalServerTick(AnimalEntity animal) {
        var hungerData = animal.getAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA);

        if (hungerData == null || !animal.isSubjectToHunger()) return;

        // Decrement the hunger countdown
        hungerData.decrementHungerCountdown(animal.isBaby() ? 2 : 1);

        // Try setting a hunger level
        if (hungerData.getHungerCountdown() <= 0) {
            if (!animal.isBaby()) {
                if (hungerData.isFullyFed()) {
                    hungerData.onBecomeFamished();
                } else if (hungerData.isFamished()) {
                    hungerData.onBecomeStarving();
                } else {
                    animal.onStarvingCountExpired();
                }

                hungerData.resetHungerCountdown();
            } else {
                // Children cannot survive being famished. They'll
                // just keep taking damage once their countdown expires
                animal.damage(animal.getDamageSources().starve(), 1);
            }
        }
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void onMobTick(CallbackInfo ci) {
        forAnimalSubclass(ChickenEntity.class, chicken -> {
            var eggData = chicken.getAttached(ModDataAttachments.CHICKEN_EGG_DATA);
            if (eggData == null) return;
            World world = chicken.getWorld();

            if (!world.isClient) {
                // set the original variable for egg laying to max int value so it's practically never reached
                chicken.eggLayTime = Integer.MAX_VALUE;

                long currentTime = world.getTime();
                if (!chicken.isBaby() /**&& isFullyFed()**/ && eggData.getTimeToLayEgg() > 0 && eggData.validateTimeToLayEgg(currentTime)) {
                    if (world.getTimeOfDay() > eggData.getTimeToLayEgg()) {
                        chicken.playSound(SoundEvents.ENTITY_SLIME_ATTACK);
                        chicken.playSound(SoundEvents.ENTITY_CHICKEN_HURT);
                        chicken.dropItem(Items.EGG);
                        eggData.resetData();
                    }
                }
            }

        });

        forAnimalSubclass(CowEntity.class, cow -> {
            var milkData = cow.getAttached(ModDataAttachments.MILK_DATA);
            if (milkData == null) return;

            if (!cow.isBaby() && !cow.getWorld().isClient) {
                milkData.tick();
            }
        });

    }


    @Inject(method = "eat", at = @At("HEAD"))
    private void onEatChicken(PlayerEntity player, Hand hand, ItemStack stack, CallbackInfo ci) {
        this.forAnimalSubclass(ChickenEntity.class, chicken -> {
            // allow feeding only of adult chickens
            if (!chicken.isBaby()) {
                long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();
                var eggData = chicken.getAttached(ModDataAttachments.CHICKEN_EGG_DATA);

                if (eggData == null) return;

                // don't try to feed if already fed
                if (eggData.getHasBeenFed()) return;

                if (stack.isIn(ItemTags.CHICKEN_FOOD)) {
                    // the following morning, at least half a day from now
                    long timeToLayEgg = (((currentTime + 12000L) / 24000L) + 1) * 24000L;

                    // crack of dawn (22550) + 30-second random variance
                    timeToLayEgg += -1450 + chicken.getRandom().nextInt(600);

                    chicken.playSound(SoundEvents.ENTITY_CHICKEN_HURT, 1.0F, chicken.getRandom().nextFloat() * 0.2F + 1.5F);

                    eggData.setTimeToLayEgg(timeToLayEgg);
                    eggData.setHasBeenFed(true);
                    chicken.setAttached(ModDataAttachments.CHICKEN_EGG_DATA, eggData);
                }
            }
        });

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
                    ModDataAttachments.CHICKEN_EGG_DATA, ChickenEggAttachedData::forDefault
            );

            // don't try to feed if already fed
            if (data != null && !data.getHasBeenFed()) {

                if (stack.isIn(ItemTags.COW_FOOD)) {
                }
            }
        }
    }

    /** Helper method to instantiate subclasses of AnimalEntity more easily **/
    @Unique
    @SuppressWarnings("unchecked")
    private <T extends AnimalEntity> void forAnimalSubclass(Class<T> type, Consumer<T> action) {
        if (type.isInstance(this)) {
            action.accept((T)(Object)this);
        }
    }

}