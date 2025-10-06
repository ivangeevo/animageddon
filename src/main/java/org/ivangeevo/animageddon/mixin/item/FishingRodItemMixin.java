package org.ivangeevo.animageddon.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.animageddon.item.interfaces.BaitableFishingRod;
import org.ivangeevo.animageddon.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin extends Item implements BaitableFishingRod
{

    public FishingRodItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V",
            shift = At.Shift.AFTER))
    private void afterDamageOnUseServerSide(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, @Local ItemStack itemStack) {
        ItemStack rodStack = user.getStackInHand(hand);

        if (!hasBaitComponent(rodStack)) return;
        int baitSlot = getBaitSlot(user, ModTags.Items.FISH_BAITS); // bait tag check
        if (baitSlot != -1) {
            // Mark the rod as baited (this sets the attachment on the bobber & the component on the item)
            this.bait(rodStack, user.fishHook);
            ItemStack baitStack = user.getInventory().getStack(baitSlot);
            if (!world.isClient) {
                baitStack.decrement(1);  // Consume one bait item

                // Play a sound to indicate bait was applied:
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }

    }

    //@Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void injectBaitLogic(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack rodStack = user.getStackInHand(hand);

        // If the rod is not baited, check for bait in the hotbar
        if (!hasBaitComponent(rodStack)) {
            int baitSlot = getBaitSlot(user, ModTags.Items.FISH_BAITS); // bait tag check
            if (baitSlot != -1) {

                ItemStack baitStack = user.getInventory().getStack(baitSlot);
                if (!world.isClient) {
                    bait(rodStack, user.fishHook);
                    baitStack.decrement(1);  // Consume one bait item
                }

                // Optionally play a sound to indicate bait was applied:
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                // Cancel further processing – the rod is now baited.
                cir.setReturnValue(TypedActionResult.success(rodStack, world.isClient()));
            }
        }

        // If the rod is baited (or no bait was found, so we just proceed with normal use)
        if (user.fishHook != null) {
            // Retrieving: reel in the hook
            if (!world.isClient) {
                int i = user.fishHook.use(rodStack);
                rodStack.damage(i, user, LivingEntity.getSlotForHand(hand));
            }
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL,
                    1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            // Casting: throw the bobber
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL,
                    0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            if (world instanceof ServerWorld serverWorld) {
                int j = (int)(EnchantmentHelper.getFishingTimeReduction(serverWorld, rodStack, user) * 20.0f);
                int k = EnchantmentHelper.getFishingLuckBonus(serverWorld, rodStack, user);
                FishingBobberEntity bobber = new FishingBobberEntity(user, world, k, j);
                serverWorld.spawnEntity(bobber);
                // If the rod was baited, transfer the bait data to the bobber and then reset the rod's bait status
                if (hasBaitComponent(rodStack)) {
                    setHasBaitComponent(rodStack, false);
                }
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            user.emitGameEvent(GameEvent.ITEM_INTERACT_START);
        }
        cir.setReturnValue(TypedActionResult.success(rodStack, world.isClient()));
    }

    /**
     * Finds a hotbar slot containing an item from the given tag.
     */
    private int getBaitSlot(PlayerEntity player, TagKey<Item> baitTag) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isIn(baitTag)) {
                return i; // Return first matching slot
            }
        }
        return -1; // Not found
    }

    @Unique
    private void bait(ItemStack stack, FishingBobberEntity bobber) {
        this.setHasBaitComponent(stack, true);
        this.setHasBaitBobber(bobber, true);
    }


}
