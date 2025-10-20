package org.ivangeevo.animageddon.mixin.entity;

import btwr.btwr_sl.tag.BTWRConventionalTags;
import com.terraformersmc.modmenu.util.mod.Mod;
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
import org.ivangeevo.animageddon.entity.interfaces.ChickenEntityAdded;
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
public abstract class ChickenEntityMixin extends AnimalEntity implements ChickenEntityAdded {

    // Chicken related variables
    @Unique private boolean hasBeenFed = false;
    @Unique private long lastFedTime = -1;
    @Unique private long lastWorldTime;
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
                this.hasBeenFed = false;
            }
        }
    }

    @Override
    protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
        if (stack.isOf(ModItems.CHICKEN_FEED) && !isBaby()) {
            long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();
            // the following morning, at least half a day from now
            timeToLayEgg = (((currentTime + 12000L) / 24000L) + 1) * 24000L;

            // crack of dawn (22550) + 30-second random variance
            timeToLayEgg += -1450 + this.random.nextInt(600);
            this.playSound(SoundEvents.ENTITY_CHICKEN_HURT, this.getSoundVolume(), this.random.nextFloat() * 0.2F + 1.5F);
            this.hasBeenFed = true;
            this.lastFedTime = currentTime;
        }

        super.eat(player, hand, stack);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putLong("TimeToLayEgg", this.timeToLayEgg);
        nbt.putLong("LastFedTime", this.lastFedTime);
        nbt.putBoolean("HasBeenFed", this.animageddon$getHasBeenFed());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("TimeToLayEgg")) {
            timeToLayEgg = nbt.getLong("TimeToLayEgg");
            lastFedTime = nbt.getLong("LastFedTime");
            this.animageddon$setHasBeenFed( nbt.getBoolean("HasBeenFed"));
        } else {
            timeToLayEgg = 0;
            lastFedTime = -1;
            this.animageddon$setHasBeenFed(false);
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

    @Override
    public boolean animageddon$getHasBeenFed() {
        return hasBeenFed;
    }

    @Override
    public void animageddon$setHasBeenFed(boolean value) {
        hasBeenFed = value;
    }

    @Override
    public long animageddon$getTimeToLayEgg() {
        return timeToLayEgg;
    }

    @Override
    public void animageddon$setTimeToLayEgg(long timeToLayEgg) {
        this.timeToLayEgg = timeToLayEgg;
    }

    @Override
    public long animageddon$getLastFedTime() {
        return lastFedTime;
    }

    @Override
    public void animageddon$setLastFedTime(long lastFedTime) {
        this.lastFedTime = lastFedTime;
    }
}
