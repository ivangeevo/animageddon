package org.ivangeevo.animageddon.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;

public class ModItems
{
    public static final Item GROUP_ANIMAGEDDON = registerItem( "group_animaggedon", new Item(new FabricItemSettings()));

    public static final Item CHICKEN_FEED = registerItem("chicken_feed", new Item(new FabricItemSettings().food(FoodComponents.ROTTEN_FLESH)));

    public static final Item EGG_RAW = registerItem("egg_raw", new Item (new FabricItemSettings().food(
                    new FoodComponent.Builder().hunger(1).meat().saturationModifier(0.002f)
                            .statusEffect( new StatusEffectInstance(StatusEffects.HUNGER, 850,2), 0.35f)
                            .build())));
    public static final Item EGG_FRIED = registerItem("egg_fried", new Item(new  FabricItemSettings().food(
                    new FoodComponent.Builder().hunger(1).saturationModifier(0.0035f)
                            .build())));

    public static final Item DUNG = registerItem( "dung", new Item(new FabricItemSettings()));
    public static final Item DUNG_GOLDEN = registerItem( "dung_golden", new Item(new FabricItemSettings()));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(AnimageddonMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AnimageddonMod.LOGGER.info("Registering Mod Items for " + AnimageddonMod.MOD_ID);

    }

}
