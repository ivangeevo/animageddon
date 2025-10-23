package org.ivangeevo.animageddon.data.interfaces;

public interface AnimalHungerData {

    /** State queries **/
    boolean isFullyFed();
    boolean isFamished();
    boolean isStarving();

    /** Configuration **/
    int getFoodValueMultiplier();
    int getGrazeHungerGain();
}
