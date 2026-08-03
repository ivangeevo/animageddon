package org.btwr.animageddon.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

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

    public static final FoodComponent BAT_WING =
            new FoodComponent.Builder()
                    .nutrition(1)
                    .saturationModifier(0.8f)
                    .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 0), 0.5f)
                    .build();

}