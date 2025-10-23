package org.ivangeevo.animageddon.data.attachments.hunger;

import net.minecraft.entity.EntityType;

import java.util.List;

public interface AnimalHungerConstants {

    /** List of animal types that can be affected by hunger  **/
    List<EntityType<?>> SUBJECT_TO_HUNGER_TYPES = List.of(
            EntityType.COW
    );

    /** Core game-time constants **/
    int FULL_HUNGER_COUNT = 24000;
    int LEVEL_UP_HUNGER_COUNT = FULL_HUNGER_COUNT + (FULL_HUNGER_COUNT / 16);
    int BASE_GRAZE_FOOD_VALUE = 200;

    int DEFAULT_FOOD_MULTIPLIER = 2;
    int PIG_FOOD_MULTIPLIER = 3;
    int CHICKEN_FOOD_MULTIPLIER = 1;

    int DEFAULT_GRAZE_DURATION = 40;
    int PIG_GRAZE_DURATION = 80;
    int CHICKEN_GRAZE_DURATION = 20;

}
