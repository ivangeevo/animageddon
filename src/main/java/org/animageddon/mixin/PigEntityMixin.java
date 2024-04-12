package org.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.animageddon.tag.ModTags;

@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends AnimalEntity {


    protected PigEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    @Unique
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.fromTag(ModTags.Items.PIG_BREEDING_ITEMS);

    @Inject(method = "initGoals", at = @At("HEAD"), cancellable = true)
    private void injectedInitGoals(CallbackInfo ci) {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.add(4, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.CARROT, Items.POTATO, Items.BEETROOT), false));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        ci.cancel();
    }


    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    private void injectedIsBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(BREEDING_INGREDIENT.test(stack));
    }


}
