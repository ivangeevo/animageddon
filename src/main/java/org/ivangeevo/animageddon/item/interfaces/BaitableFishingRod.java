package org.ivangeevo.animageddon.item.interfaces;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import org.ivangeevo.animageddon.ModAttachments;

import static org.ivangeevo.animageddon.item.ModComponents.HAS_BAIT_COMPONENT;

public interface BaitableFishingRod {

    default void setHasBaitBobber(FishingBobberEntity bobber, boolean value) {
        bobber.setAttached(ModAttachments.HAS_BAIT_ATTACHMENT, value);
    }

    default boolean hasBaitBobber(FishingBobberEntity bobber) {
        return Boolean.TRUE.equals(bobber.getAttached(ModAttachments.HAS_BAIT_ATTACHMENT));
    }

    default void setHasBaitComponent(ItemStack stack, boolean value) {
        stack.set(HAS_BAIT_COMPONENT, value);
    }

    default boolean hasBaitComponent(ItemStack stack) {
        return stack.contains(HAS_BAIT_COMPONENT) && stack.getOrDefault(HAS_BAIT_COMPONENT, false);
    }

}
