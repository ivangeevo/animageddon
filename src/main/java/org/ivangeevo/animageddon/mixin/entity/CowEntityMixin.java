package org.ivangeevo.animageddon.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.ai.goal.GrazeGoal;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
        this.goalSelector.add(6, new GrazeGoal(this));
    }

    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    private void onIsBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(stack.isOf(Items.CAKE));
    }

    @Override
    public boolean isSubjectToHunger() { return true; }

}