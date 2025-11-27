package org.btwr.animageddon.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

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

}