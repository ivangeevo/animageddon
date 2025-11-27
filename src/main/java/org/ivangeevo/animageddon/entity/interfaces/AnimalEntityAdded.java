package org.ivangeevo.animageddon.entity.interfaces;

import net.minecraft.util.math.BlockPos;

public interface AnimalEntityAdded  {

    boolean getWearingBreedingHarness();

    //-------------- Hunger related functionality ------------//

    default boolean isSubjectToHunger() {
        return false;
    }

    default void initHungerWithVariance() {}

    default void onStarvingCountExpired() {}

    default void addToHungerCount(int addedHunger) {}

    //-------------- Grazing related functionality ------------//

    default BlockPos getGrazeBlockForPos() {
        return null;
    }

    default boolean canGrazeOnBlock(BlockPos pos) {
        return false;
    }

    default void onGrazeBlock(BlockPos pos) {}

    default boolean isHungryEnoughToGraze() {
        return false;
    }

    default boolean shouldNotifyBlockOnGraze() {
        return false;
    }

    default int getGrazeProgressCounter() {
        return 0;
    }

    default void setGrazeProgressCounter(int value) {}

}