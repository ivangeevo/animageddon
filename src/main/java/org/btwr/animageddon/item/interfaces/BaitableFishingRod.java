package org.btwr.animageddon.item.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import org.btwr.animageddon.AnimageddonMod;
import org.btwr.animageddon.data.ModDataAttachments;

import static org.btwr.animageddon.item.ModComponents.HAS_BAIT_COMPONENT;

public interface BaitableFishingRod {

    default void setHasBait(FishingBobberEntity bobber, boolean value) {
        bobber.setAttached(ModDataAttachments.HAS_BAIT, value);
    }

    default boolean hasBait(FishingBobberEntity bobber) {
        if (Boolean.TRUE.equals(bobber.getAttached(ModDataAttachments.HAS_BAIT))){
            return true;
        }

        AnimageddonMod.LOGGER.warn("No bait attached");
        return false;
    }

    default void setHasBait(ItemStack stack, boolean value) {
        stack.set(HAS_BAIT_COMPONENT, value);
    }

    default boolean hasBaitComponent(ItemStack stack) {
        return stack.contains(HAS_BAIT_COMPONENT) && stack.getOrDefault(HAS_BAIT_COMPONENT, false);
    }

    /**
     * Finds a hotbar slot containing an item
     */
    default int getBaitSlot(PlayerEntity player, Item bait) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(bait)) {
                return i; // Return first matching slot
            }
        }
        return -1; // Not found
    }
    /**
     * Finds a hotbar slot containing an item from a tag
     */
    default int getBaitSlot(PlayerEntity player, TagKey<Item> baitTag) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isIn(baitTag)) {
                return i; // Return first matching slot
            }
        }
        return -1; // Not found
    }

    default void bait(ItemStack stack, FishingBobberEntity bobber) {
        this.setHasBait(stack, true);
        this.setHasBait(bobber, true);
    }

}