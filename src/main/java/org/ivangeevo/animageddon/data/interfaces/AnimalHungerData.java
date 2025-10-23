package org.ivangeevo.animageddon.data.interfaces;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;

public interface AnimalHungerData {

    /** State transitions **/
    void tick(AnimalEntity animal);
    void onStarvingCountExpired(AnimalEntity animal);
    void onGrazeBlock(AnimalEntity animal, BlockPos pos);
    void addToHungerCount(AnimalEntity animal, int value);

    /** State queries **/
    boolean isHungryEnoughToGraze();
    boolean isFullyFed();
    boolean isFamished();
    boolean isStarving();

    /** Configuration **/
    int getFoodValueMultiplier();
    int getGrazeHungerGain();
}
