package org.btwr.animageddon.item;

import net.minecraft.component.type.FoodComponent;

public class ModFoodComponents {

    public static final FoodComponent CURED_MEAT =
            new FoodComponent.Builder()
                    .nutrition(3)
                    .saturationModifier(0.1f)
                    .build();

    public static final FoodComponent BURNED_MEAT =
            new FoodComponent.Builder()
                    .nutrition(3)
                    .saturationModifier(0.1f)
                    .build();

    public static final FoodComponent CHEVAL =
            new FoodComponent.Builder()
                    .nutrition(3)
                    .saturationModifier(0.3f)
                    .build();

    public static final FoodComponent COOKED_CHEVAL =
            new FoodComponent.Builder()
                    .nutrition(8)
                    .saturationModifier(0.8f)
                    .build();

}