package org.ivangeevo.animageddon.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin extends Item {

    public FishingRodItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void injectBaitLogic(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack rodStack = user.getStackInHand(hand);

        // If the rod is not baited, check for bait in the hotbar
        if (!hasBait(user)) {
            int baitSlot = getBaitSlot(user, ItemTags.FISHES); // bait tag check
            if (baitSlot != -1) {
                ItemStack baitStack = user.getInventory().getStack(baitSlot);

                /**
                if (!world.isClient && user.fishHook.state == State.B) {
                    setHasBait(user, true);  // Mark the rod as baited (this sets the attachment on the bobber later)
                    baitStack.decrement(1);  // Consume one bait item
                }
                 **/
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
                if (hasBait(user)) {
                    bobber.setAttached(ModDataAttachments.HAS_BAIT, true);
                    setHasBait(user, false);
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
    private static void setHasBait(PlayerEntity user, boolean value) {
        if (!user.getWorld().isClient) { // Ensure this runs on the server side
            FishingBobberEntity bobber = user.fishHook;
            if (bobber != null) {
                bobber.setAttached(ModDataAttachments.HAS_BAIT, value);
                System.out.println("Set hasBait to " + value + " on bobber " + bobber);
            } else {
                System.out.println("Bobber entity is null!");
            }
        }
    }


    @Unique
    private static boolean hasBait(PlayerEntity user) {
        FishingBobberEntity bobber = user.fishHook;
        if (bobber != null) {
            return Boolean.TRUE.equals(user.fishHook.getAttached(ModDataAttachments.HAS_BAIT));
        }
        return false;
    }

    private boolean hasItemInHotbar(PlayerEntity player, Item targetItem) {
        for (int i = 0; i < 9; i++) { // Hotbar slots are 0-8
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == targetItem) {
                return true; // Found the item
            }
        }
        return false; // Item not found in the hotbar
    }

    private boolean hasItemInHotbar(PlayerEntity player, TagKey<Item> targetItem) {
        for (int i = 0; i < 9; i++) { // Hotbar slots are 0-8
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isIn(targetItem)) {
                return true; // Found the item
            }
        }
        return false; // Item not found in the hotbar
    }


}
