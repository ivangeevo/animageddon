package org.ivangeevo.animageddon.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.Block;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.data.attachments.ChickenEggAttachedData;
import org.ivangeevo.animageddon.data.attachments.CowMilkAttachedData;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.data.attachments.hunger.AnimalHungerAttachedData;
import org.ivangeevo.animageddon.entity.interfaces.AnimalEntityAdded;
import org.ivangeevo.animageddon.tag.ModTags;
import org.ivangeevo.animageddon.util.ServerTimeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static org.ivangeevo.animageddon.data.attachments.hunger.AnimalHungerConstants.FULL_HUNGER_COUNT;
import static org.ivangeevo.animageddon.data.attachments.hunger.AnimalHungerConstants.LEVEL_UP_HUNGER_COUNT;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements AnimalEntityAdded {

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public int grazeProgressCounter = 0;

    @Override
    public int getGrazeProgressCounter() {
        return grazeProgressCounter;
    }

    @Override
    public void setGrazeProgressCounter(int value) {
        grazeProgressCounter = value;
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
        forAnimalSubclass(AnimalEntity.class, animal -> {
            var hungerData = animal.getAttachedOrCreate(ModDataAttachments.ANIMAL_HUNGER_DATA, AnimalHungerAttachedData::forDefault);
            animal.setAttached(ModDataAttachments.ANIMAL_HUNGER_DATA, hungerData);
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

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void onMobTick1(CallbackInfo ci) {
        /**
        this.forAnimalSubclass(CowEntity.class, cow -> {
            var hungerData = cow.getAttached(ModDataAttachments.ANIMAL_HUNGER_DATA);

            if (!isSubjectToHunger(cow)) return;

            int hungerLevel = hungerData.getHungerLevel();
            int hungerCountdown = hungerData.getHungerCountdown();

            hungerData.setHungerCountdown(hungerCountdown - (cow.isBaby() ? 2 : 1));

            if (hungerCountdown <= 0) {
                if (this.getWorld().isClient) return;

                if (!cow.isBaby()) {
                    if (hungerLevel == 0) {
                        switch (hungerData.getHungerLevel()) {
                            case 0 -> hungerData.onBecomeFamished();
                            case 1 -> hungerData.onBecomeStarving();
                            case 2 -> cow.onStarvingCountExpired();
                        }
                        hungerData.resetHungerCountdown();
                    } else {
                        // children can't survive being famished. they'll
                        // just keep taking damage once their countdown expires
                        cow.damage(cow.getDamageSources().starve(), 1);
                    }
                }
            }
        });
         **/

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

        /**
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
         **/
    }

    @Override
    public void initHungerWithVariance() {
        // prevent initially spawned animals from all eating at the same time.
        AnimalEntity animal = (AnimalEntity)(Object)this;

        var data = animal.getAttached(ModDataAttachments.ANIMAL_HUNGER_DATA);
        if (!animal.hasAttached(ModDataAttachments.ANIMAL_HUNGER_DATA) || data == null) return;

        if (isSubjectToHunger(animal)) {
            data.setHungerCountdown(FULL_HUNGER_COUNT - animal.getRandom().nextInt(data.getGrazeHungerGain()));
        }
    }

    @Override
    public boolean canGrazeOnBlock(BlockPos pos) {
        World world = this.getWorld();
        Block block = world.getBlockState(pos).getBlock();

        if (block != null) {
            return block.canBeGrazedOn(world, pos, (AnimalEntity)(Object)this);
        }

        return false;
    }

    @Override
    public BlockPos getGrazeBlockForPos() {
        BlockPos pos = this.getBlockPos();
        BlockPos targetPos = new BlockPos(
                MathHelper.floor(pos.getX()),
                (int)this.getBoundingBox().minY,
                MathHelper.floor(pos.getZ())
        );

        if (this.canGrazeOnBlock(targetPos)) {
            return targetPos;
        } else {
            //targetPos.y--;
            BlockPos newTargetPos = targetPos.down();

            if (canGrazeOnBlock(newTargetPos) ) {
                return targetPos;
            }
        }

        return null;
    }

    @Override
    public boolean isHungryEnoughToGraze() {
        var hungerData = this.getAttached(ModDataAttachments.ANIMAL_HUNGER_DATA);
        if (hungerData == null) return false;
        return !hungerData.isFullyFed() || hungerData.getHungerCountdown() + hungerData.getGrazeHungerGain() <= FULL_HUNGER_COUNT;
    }

    @Override
    public boolean shouldNotifyBlockOnGraze() {
        return true;
    }

    @Override
    public void onStarvingCountExpired() {
        // max health 20 wolves, 15 cows, 10 pigs, 8 sheep, 4 chicken
        // Keep the if check for when we add difficulty checks
        //if (this.getWorld().getDifficulty().canAnimalsStarve()) {
        this.damage(this.getDamageSources().starve(), 5);
        //}
    }

    @Override
    public void addToHungerCount(int addedHunger) {
        var hungerData = this.getAttached(ModDataAttachments.ANIMAL_HUNGER_DATA);
        if (hungerData == null) return;
        int hungerCountdown = hungerData.getHungerCountdown();

        //hungerCountdown += iAddedHunger;
        hungerData.setHungerCountdown(hungerCountdown + addedHunger);

        // don't level up immediately when full to prevent flickering state

        if (hungerCountdown > LEVEL_UP_HUNGER_COUNT)
        {
            int hungerLevel = hungerData.getHungerLevel();

            if (hungerLevel > 0) {
                //hungerCountdown -= FULL_HUNGER_COUNT;
                hungerData.setHungerCountdown(hungerCountdown - FULL_HUNGER_COUNT);

                //setHungerLevel(hungerLevel - 1);
                hungerData.setHungerLevel(hungerLevel - 1);
            }
        }
    }

    @Override
    public void onGrazeBlock(BlockPos pos) {
        var hungerData = this.getAttached(ModDataAttachments.ANIMAL_HUNGER_DATA);
        if (hungerData == null) return;
        addToHungerCount(hungerData.getGrazeHungerGain());
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

    @Unique
    private boolean isSubjectToHunger(AnimalEntity animal) {
        return animal.getType().isIn(ModTags.EntityTypes.SUBJECT_TO_HUNGER_ANIMALS);
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