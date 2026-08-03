package org.btwr.animageddon.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import org.btwr.animageddon.item.ModComponents;
import org.btwr.animageddon.tag.ModTags;
import org.jetbrains.annotations.Nullable;

public class ModItemUseEvents {

    public static void register() {

        // Register special interaction for a fishing rod to allow it to be baited with fishing bait items
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (world.isClient) return TypedActionResult.pass(stack);

            if (stack.isOf(Items.FISHING_ROD)) {
                boolean hasBait = Boolean.TRUE.equals(stack.get(ModComponents.HAS_BAIT_COMPONENT));

                if (!hasBait) {
                    ItemStack baitSlotStack = getFishBait(player);

                    if (baitSlotStack != null) {
                        world.playSound(
                                null,
                                player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ENTITY_SLIME_ATTACK,
                                player.getSoundCategory(),
                                0.5F,
                                0.4F / (world.random.nextFloat() * 0.4F + 0.8F)
                        );

                        baitSlotStack.decrement(1);
                        stack.set(ModComponents.HAS_BAIT_COMPONENT, true);

                        return TypedActionResult.success(stack);
                    }
                }
            }

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
