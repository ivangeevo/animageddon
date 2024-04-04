package org.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.animageddon.item.ModItems;
import org.animageddon.tag.ModTags;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity {
    @Shadow public float prevFlapProgress;
    @Shadow public float flapProgress;
    @Shadow public float prevMaxWingDeviation;
    @Shadow public float maxWingDeviation;
    @Shadow public float flapSpeed;
    @Shadow public boolean hasJockey;

    @Shadow public int eggLayTime;


    private boolean hasBeenFed = false;
    private long lastFedTime = -1;
    private static final String TIME_TO_LAY_EGG_TAG = "TimeToLayEgg"; // NBT tag name
    private long timeToLayEgg = 0;
    private long lastWorldTime = -1;

    protected ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("HEAD"), cancellable = true)
    private void injectedInitGoals(CallbackInfo ci) {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.0, Ingredient.fromTag(ModTags.Items.CHICKEN_TEMPT_ITEMS), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        ci.cancel();
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void injectedTickMovement(CallbackInfo ci) {
        super.tickMovement();
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation += (this.isOnGround() ? -1.0f : 4.0f) * 0.3f;
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0f, 1.0f);
        if (!this.isOnGround() && this.flapSpeed < 1.0f) {
            this.flapSpeed = 1.0f;
        }
        this.flapSpeed *= 0.9f;
        Vec3d vec3d = this.getVelocity();
        if (!this.isOnGround() && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }

        long worldTime = getWorld().getTimeOfDay() % 24000;

        // Check if it's sunrise
        if (worldTime < lastWorldTime) {

            if (hasBeenFed) {
                // The chicken has been fed, lay an egg
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.dropItem(Items.EGG);
                this.emitGameEvent(GameEvent.ENTITY_PLACE);
                hasBeenFed = false;
            }
        }

        lastWorldTime = worldTime;

        ci.cancel();
    }



    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getStackInHand(hand);

        if (!hasBeenFed) {
            if (heldItem.getItem() == ModItems.CHICKEN_FEED) {

                if (!player.isCreative()) {
                    heldItem.decrement(1);
                }

                hasBeenFed = true;
                player.swingHand(hand);

                // Play the eating sound
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, this.random.nextFloat() * 0.1F + 0.9F);
                player.getStackInHand(hand).setNbt(new NbtCompound());
                return ActionResult.success(getWorld().isClient);
            }
        } else {
            return  ActionResult.FAIL;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putLong(TIME_TO_LAY_EGG_TAG, this.timeToLayEgg);
        tag.putLong("LastFedTime", this.lastFedTime);
        tag.putBoolean("HasBeenFed", this.hasBeenFed);
        return tag;
    }





    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains(TIME_TO_LAY_EGG_TAG)) {
            timeToLayEgg = tag.getLong(TIME_TO_LAY_EGG_TAG);
            lastFedTime = tag.getLong("LastFedTime");
            hasBeenFed = tag.getBoolean("HasBeenFed");
        } else {
            timeToLayEgg = 0;
            lastFedTime = -1;
            hasBeenFed = false;
        }

    }
}
