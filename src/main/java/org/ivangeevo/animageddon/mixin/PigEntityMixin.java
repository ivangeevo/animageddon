package org.ivangeevo.animageddon.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.ivangeevo.animageddon.tag.ModTags;

@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends AnimalEntity {

    protected PigEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique private static final boolean isVegehennaLoaded = FabricLoader.getInstance().isModLoaded("vegehenna");
    @Unique private static final Ingredient BREEDING_INGREDIENT =
            isVegehennaLoaded ? Ingredient.fromTag(ModTags.Items.PIG_BREEDING_ITEMS) : Ingredient.fromTag(ItemTags.PIG_FOOD);

    // Temporary solution (permanent if it works) setting the breeding ingredient to one that Vegehenna mod provides -
    // which is the chocolate item from that mod, else use the default ingredient.
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void injectedInitGoals(CallbackInfo ci) {
        this.goalSelector.add(4, new TemptGoal(this, 1.4, BREEDING_INGREDIENT, false));
    }

    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    private void injectedIsBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(isVegehennaLoaded ? stack.isIn(ModTags.Items.PIG_BREEDING_ITEMS) : stack.isIn(ItemTags.PIG_FOOD));
    }

}
