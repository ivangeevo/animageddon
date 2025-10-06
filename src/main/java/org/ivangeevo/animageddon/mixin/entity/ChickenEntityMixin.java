package org.ivangeevo.animageddon.mixin.entity;

import btwr.btwr_sl.tag.BTWRConventionalTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.util.ServerTimeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity {

    // Chicken related variables
    @Unique private boolean hasBeenFed = false;
    @Unique private long lastFedTime = -1;
    @Unique private long lastWorldTime = -1;
    @Unique private long timeToLayEgg = 0;

    @Shadow public int eggLayTime;

    protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        lastWorldTime = world.getTimeOfDay();
    }

    @Inject(method = "isBreedingItem", at = @At("RETURN"), cancellable = true)
    private void setBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(stack.isOf(ModItems.CHICKEN_FEED));
    }

    //@Inject(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 3))
    private void modifyTemptGoal(CallbackInfo ci) {
        // Set canBeScared to true
        TemptGoal customTemptGoal =
                new TemptGoal(this, 1.0, stack -> stack.isIn(BTWRConventionalTags.Items.CHICKEN_TEMPT_ITEMS), true);

        this.goalSelector.add(3, customTemptGoal);
    }

    //@Inject(method = "initGoals", at = @At("TAIL"))
    private void addBreedingGoal(CallbackInfo ci) {
        // Set canBeScared to false for breeding items
        TemptGoal customBreedingGoal =
                new TemptGoal(this, 1.25, stack -> stack.isOf(ModItems.CHICKEN_FEED), false);

        this.goalSelector.add(3, customBreedingGoal);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        // set the original egg lay time to max int value to practically make it never reach 0 (and lay an egg)
        this.eggLayTime = Integer.MAX_VALUE;

        if (!this.isBaby() /**&& isFullyFed()**/ && timeToLayEgg > 0 && validateTimeToLayEgg(this.getWorld())) {
            if (this.getWorld().getTimeOfDay() > timeToLayEgg) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK,1.0f, this.getSoundPitch());
                this.playSound(SoundEvents.ENTITY_CHICKEN_HURT,1.0f, this.getSoundPitch());
                this.dropItem(Items.EGG);
                this.timeToLayEgg = 0;

            }
        }
    }

    @Override
    protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
        if (stack.isOf(ModItems.CHICKEN_FEED) && !isBaby()) {
            long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();
            // following morning, at least half day from now
            timeToLayEgg = (((currentTime + 12000L) / 24000L) + 1) * 24000L;

            // crack of dawn (22550) + 30 seconds random variance
            timeToLayEgg += -1450 + this.random.nextInt(600);
            this.playSound(SoundEvents.ENTITY_CHICKEN_HURT, this.getSoundVolume(), this.random.nextFloat() * 0.2F + 1.5F);
        }
        super.eat(player, hand, stack);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putLong("TimeToLayEgg", this.timeToLayEgg);
        nbt.putLong("LastFedTime", this.lastFedTime);
        nbt.putBoolean("HasBeenFed", this.getHasBeenFed());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("TimeToLayEgg")) {
            timeToLayEgg = nbt.getLong("TimeToLayEgg");
            lastFedTime = nbt.getLong("LastFedTime");
            setHasBeenFed( nbt.getBoolean("HasBeenFed") ); ;
        } else {
            timeToLayEgg = 0;
            lastFedTime = -1;
            setHasBeenFed(false);
        }
    }

    @Unique
    private boolean validateTimeToLayEgg(World world) {
        long currentTime = world.getTime();
        long deltaTime = timeToLayEgg - currentTime;

        if (deltaTime > 48000L) {
            // we're more than 2 days before the time, something is wrong (like a time change command), so don't lay
            timeToLayEgg = 0;

            return false;
        }

        return true;
    }

    public boolean getHasBeenFed() {
        return hasBeenFed;
    }

    public void setHasBeenFed(boolean value) {
        hasBeenFed = value;
    }


}
