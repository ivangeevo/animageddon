package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.interfaces.AnimalEntityAdded;
import org.ivangeevo.animageddon.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements AnimalEntityAdded {

    @Shadow protected abstract void eat(PlayerEntity player, Hand hand, ItemStack stack);

    @Unique private boolean isChickenEntity = (AnimalEntity)(Object)this instanceof ChickenEntity;


    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }


    // TODO: Figure out why the nbt data for hasBeenFed doesn't persist between world restarts.
    //@Inject(method = "interactMob", at = @At("TAIL"), cancellable = true)
    private void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {

        if (isChickenEntity) {
            ItemStack heldItem = player.getStackInHand(hand);

            if (!getHasBeenFed()) {
                if (heldItem.getItem() == ModItems.CHICKEN_FEED) {
                    this.eat(player, player.getActiveHand(), heldItem);

                    setHasBeenFed(true);
                    player.swingHand(hand);

                    // Play the eating sound
                    playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F, this.random.nextFloat() * 0.1F + 0.9F);
                    //player.getStackInHand(hand).setNbt(new NbtCompound());
                    cir.setReturnValue(ActionResult.success(getWorld().isClient));
                }
            } else {
                cir.setReturnValue(ActionResult.FAIL);
            }

        }
    }

}