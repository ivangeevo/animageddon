package org.ivangeevo.animageddon.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;

public class ModItems
{
    public static final Item GROUP_ANIMAGEDDON = registerItem( "group_animaggedon", new Item(new Item.Settings()));

    public static final Item NITRE = registerItem( "nitre", new Item(new Item.Settings()));

    public static final Item CURED_MEAT = registerItem( "cured_meat", new Item(new Item.Settings().maxCount(16).food(ModFoodComponents.CURED_MEAT)));

    public static final Item BURNED_MEAT = registerItem( "burned_meat", new Item(new Item.Settings().maxCount(16).food(ModFoodComponents.BURNED_MEAT)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(AnimageddonMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AnimageddonMod.LOGGER.info("Registering Mod Items for " + AnimageddonMod.MOD_ID);

    }

}
