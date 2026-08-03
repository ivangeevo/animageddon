package org.btwr.animageddon.mixin.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.btwr.animageddon.item.interfaces.BaitableFishingRod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin extends Item implements BaitableFishingRod {

    public FishingRodItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void injectBaitLogic(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        FishingRodItem self = (FishingRodItem)(Object)this;

        ItemStack rodStack = user.getStackInHand(hand);

        if (user.fishHook != null) {
            if (self.hasBait(user.fishHook)) {
                int itemDamage = user.fishHook.use(rodStack);

                // need to reset stack as you lose bait when you catch a fish

                rodStack.damage(itemDamage, user, LivingEntity.getSlotForHand(hand));
                cir.setReturnValue(TypedActionResult.success(rodStack));
            }

        } else {
            world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS);

            if (!world.isClient) {
                FishingBobberEntity bobber = new FishingBobberEntity(EntityType.FISHING_BOBBER, world);
                bobber.setHasBait(bobber, true);
                world.spawnEntity(bobber);
            }
            cir.setReturnValue(TypedActionResult.success(rodStack));
        }

    }



}