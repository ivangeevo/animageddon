package org.ivangeevo.animageddon.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;
import org.ivangeevo.animageddon.block.ModBlocks;

public class ModItems
{
    //public static final Item GROUP_ANIMAGEDDON = registerItem( "group_animaggedon", new Item(new Item.Settings()));

    public static final Item CHICKEN_FEED = registerItem( "chicken_feed", new Item(new Item.Settings()));

    public static final Item NITRE = registerItem( "nitre", new Item(new Item.Settings()));

    public static final Item CURED_MEAT = registerItem( "cured_meat", new Item(new Item.Settings().maxCount(16).food(ModFoodComponents.CURED_MEAT)));

    public static final Item BURNED_MEAT = registerItem( "burned_meat", new Item(new Item.Settings().maxCount(16).food(ModFoodComponents.BURNED_MEAT)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(AnimageddonMod.MOD_ID, name), item);
    }

    public static void registerModItemsAndAddToGroups() {
        AnimageddonMod.LOGGER.info("Registering Mod Items for " + AnimageddonMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries ->
        {
            entries.add(CURED_MEAT);
            entries.add(BURNED_MEAT);
            entries.add(CHICKEN_FEED);

        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->
        {
            entries.add(NITRE);
        });

    }

}
