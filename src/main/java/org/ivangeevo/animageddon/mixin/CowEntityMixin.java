package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.PathAwareEntity;
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
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity implements CowEntityAdded
{
    @Shadow public abstract ActionResult interactMob(PlayerEntity player, Hand hand);

    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "initGoals",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V",
                    ordinal = 3))
    private void modifyTemptGoal(CallbackInfo ci) {
        TemptGoal customTemptGoal = new TemptGoal(
                this,
                1.25,
                stack -> stack.isIn(ItemTags.COW_FOOD),
                true // Custom `canBeScared` value
        );

        this.goalSelector.add(3, customTemptGoal);
    }


    @Inject(method = "initGoals", at = @At("TAIL"))
    private void injectedInitGoals(CallbackInfo ci)
    {
        this.goalSelector.add(3,
                new TemptGoal(this, 1.4, Ingredient.ofItems(Items.CAKE), false));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() == Items.CAKE;
    }


}