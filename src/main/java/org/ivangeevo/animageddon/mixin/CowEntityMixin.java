package org.ivangeevo.animageddon.mixin;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.AnimageddonMod;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity implements CowEntityAdded {
    @Shadow public abstract ActionResult interactMob(PlayerEntity player, Hand hand);



    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    // Setting "canBeScared" to true for non-breeding tempt items
    @Inject(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 3))
    private void modifyTemptGoal(CallbackInfo ci) {
        TemptGoal customTemptGoal = new TemptGoal(this, 1.25, stack -> stack.isIn(ItemTags.COW_FOOD), true);
        this.goalSelector.add(3, customTemptGoal);
    }

    // making Cake a viable "tempt" item
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void injectedInitGoals(CallbackInfo ci) {
        this.goalSelector.add(3, new TemptGoal(this, 1.4, Ingredient.ofItems(Items.CAKE), false));
    }

    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    private void onIsBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(stack.isOf(Items.CAKE));
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void injectedInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if (!this.isBaby() && stack.getItem() == Items.BUCKET) {
            if (gotMilk()) {
                stack.decrement(1);

                if (stack.isEmpty()) {
                    player.setStackInHand(hand, new ItemStack(Items.MILK_BUCKET));
                } else {
                    if (!player.getInventory().insertStack(new ItemStack(Items.MILK_BUCKET))) {
                        player.dropItem(Items.MILK_BUCKET.getDefaultStack(), false);
                    }
                }

                tryAttack(this);

                if (!getWorld().isClient) {
                    setGotMilk(false);
                    this.setAttached(ModDataAttachments.MILK_COOLDOWN, 0);
                    this.getWorld().playSound(
                            null, this.getBlockPos(), SoundEvents.ENTITY_SLIME_ATTACK, SoundCategory.NEUTRAL,
                            1.0F, (getWorld().random.nextFloat() - getWorld().random.nextFloat()) * 0.2F + 0.6F
                    );
                }


                cir.setReturnValue(ActionResult.success(getWorld().isClient));
            }
        }
        cir.setReturnValue(ActionResult.PASS);
    }


    @Override
    public boolean gotMilk() {
        return Boolean.TRUE.equals(this.getAttachedOrSet(ModDataAttachments.GOT_MILK));
    }

    @Override
    public void setGotMilk(boolean bGotMilk) {
        this.setAttached(ModDataAttachments.GOT_MILK, bGotMilk);
    }
}