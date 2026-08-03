package org.btwr.animageddon.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import org.btwr.animageddon.item.ModComponents;
import org.btwr.animageddon.tag.ModTags;
import org.jetbrains.annotations.Nullable;

public class ModItemUseEvents {

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (!stack.isOf(Items.FISHING_ROD)) {
                return TypedActionResult.pass(stack);
            }

            boolean hasBait = Boolean.TRUE.equals(stack.get(ModComponents.HAS_BAIT_COMPONENT));

            // --- Reeling in / catching a fish (mirrors BTW's fishEntity != null branch) ---
            // We only need to intervene here when baited, so we can clear the bait
            // component after the catch (mirrors BTW resetting to the unbaited rod item).
            // If unbaited, just let vanilla's own use() handle the reel normally.
            if (player.fishHook != null) {
                if (!hasBait) {
                    return TypedActionResult.pass(stack);
                }

                if (!world.isClient) {
                    int damage = player.fishHook.use(stack);

                    if (damage > 0) {
                        stack.remove(ModComponents.HAS_BAIT_COMPONENT);
                    }

                    stack.damage(damage, player, LivingEntity.getSlotForHand(hand));
                }

                player.swingHand(hand);
                return TypedActionResult.success(stack);
            }

            // --- Applying bait ---
            if (!hasBait) {
                ItemStack baitStack = getFishBait(player);

                if (baitStack != null) {
                    world.playSound(
                            null,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_SLIME_ATTACK,
                            SoundCategory.PLAYERS,
                            0.5F,
                            0.4F / (world.random.nextFloat() * 0.4F + 0.8F)
                    );

                    baitStack.decrement(1);
                    stack.set(ModComponents.HAS_BAIT_COMPONENT, true);

                    return TypedActionResult.success(stack);
                }
            }

            // --- No bait applied (already baited, or none found) -> normal cast ---
            // Let vanilla FishingRodItem#use() handle spawning the bobber and playing the cast sound
            return TypedActionResult.pass(stack);
        });
    }

    private static @Nullable ItemStack getFishBait(PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        ItemStack baitSlotStack = null;

        // Hotbar: slots 0-8
        for (int i = 0; i <= 8; i++) {
            ItemStack candidate = inv.main.get(i);
            if (!candidate.isEmpty() && candidate.isIn(ModTags.Items.FISH_BAITS)) {
                baitSlotStack = candidate;
                break;
            }
        }

        // Off-hand, only if hotbar had none
        if (baitSlotStack == null) {
            ItemStack offHandStack = inv.offHand.getFirst();
            if (!offHandStack.isEmpty() && offHandStack.isIn(ModTags.Items.FISH_BAITS)) {
                baitSlotStack = offHandStack;
            }
        }
        return baitSlotStack;
    }

}
