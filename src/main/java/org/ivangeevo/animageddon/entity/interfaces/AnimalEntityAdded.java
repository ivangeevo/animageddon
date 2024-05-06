package org.ivangeevo.animageddon.entity.interfaces;

import net.minecraft.item.ItemStack;
import org.ivangeevo.animageddon.util.MiscUtils;

public interface AnimalEntityAdded  {

    void setWearingBreedingHarness( boolean bWearingHarness );
    void onEatBreedingItem();

    void setInLove();



    boolean getWearingBreedingHarness();
    boolean isEdibleItem(ItemStack stack);
    boolean attemptToEatItemForBreeding(ItemStack stack);
    boolean isReadyToEatBreedingItem();
    boolean isFullyFed();



    int getFoodValueMultiplier();

    int getHungerLevel();

    //-------------- Hunger related functionality ------------//

    int FULL_HUNGER_COUNT = ( MiscUtils.TICKS_PER_GAME_DAY );
    int hungerCountdown();
    void setHungerCountdown( int value );



    int getInLove();

}
