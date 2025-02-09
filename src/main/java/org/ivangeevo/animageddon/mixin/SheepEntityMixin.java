package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity implements Shearable {

    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 3), cancellable = true)
    private void modifyTemptGoal(CallbackInfo ci) {
        // Set canBeScared to true
        TemptGoal customTemptGoal =
                new TemptGoal(this, 1.25, stack -> stack.isIn(ItemTags.SHEEP_FOOD), true);

        this.goalSelector.add(3, customTemptGoal);
        ci.cancel();
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void injectedInitGoals(CallbackInfo ci) {
        this.goalSelector.add(3, new TemptGoal(this, 1.4, Ingredient.ofItems(Items.PUMPKIN_PIE), false));
    }

    // Change the breeding item to Pumpkin Pie.
    @Override public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.PUMPKIN_PIE;
    }

}
