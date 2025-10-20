package org.ivangeevo.animageddon.entity.interfaces;

public interface ChickenEntityAdded {

    default long animageddon$getTimeToLayEgg() {
        return 0;
    }
    default void animageddon$setTimeToLayEgg(long value) {}

    default boolean animageddon$getHasBeenFed(){
        return false;
    }
    default void animageddon$setHasBeenFed(boolean value) {}

    default long animageddon$getLastFedTime() {
        return 0;
    }
    default void animageddon$setLastFedTime(long value) {}

}
