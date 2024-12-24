package org.ivangeevo.animageddon.mixin;

import btwr.btwrsl.tag.BTWRConventionalTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.animageddon.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity {

    @Shadow public abstract boolean hasJockey();

    // Chicken related variables
    @Unique private boolean hasBeenFed = false;
    @Unique private long lastFedTime = -1;
    @Unique private long timeToLayEgg = 0;

    @Unique private long lastWorldTime = -1;

    protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean getHasBeenFed() {
        return hasBeenFed;
    }

    @Override
    public void setHasBeenFed(boolean value) {
        hasBeenFed = value;
    }

    @Inject(method = "initGoals", at = @At("HEAD"), cancellable = true)
    private void injectedInitGoals(CallbackInfo ci) {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.0, Ingredient.fromTag(BTWRConventionalTags.Items.CHICKEN_TEMPT_ITEMS), false));
        this.goalSelector.add(3, new TemptGoal(this, 1.2, Ingredient.ofItems(ModItems.CHICKEN_FEED), false));

        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));

        ci.cancel();
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/ChickenEntity;getWorld()Lnet/minecraft/world/World;"), cancellable = true)
    private void onTickMovement(CallbackInfo ci) {

        long worldTime = getWorld().getTimeOfDay() % 24000;

        // Check if it's sunrise
        if (worldTime < lastWorldTime) {

            if (this.getHasBeenFed() && !this.getWorld().isClient() && this.isAlive() && !this.isBaby() && !this.hasJockey()) {
                // The chicken has been fed, lay an egg
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.dropItem(Items.EGG);
                this.emitGameEvent(GameEvent.ENTITY_PLACE);
                this.setHasBeenFed(false);
            }
        }

        lastWorldTime = worldTime;

        ci.cancel();
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


}
