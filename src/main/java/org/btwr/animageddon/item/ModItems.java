package org.btwr.animageddon.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;
import org.btwr.animageddon.item.items.WebUntanglingItem;
import org.btwr.shared_library.api.item.ProgressiveCraftingItem;

public class ModItems {

    public static final Item CHICKEN_FEED = registerItem( "chicken_feed", new Item(new Item.Settings()));
    public static final Item NITRE = registerItem( "nitre", new Item(new Item.Settings()));
    public static final Item CURED_MEAT = registerItem( "cured_meat", new Item(new Item.Settings().food(ModFoodComponents.CURED_MEAT)));
    public static final Item BURNED_MEAT = registerItem( "burned_meat", new Item(new Item.Settings().food(ModFoodComponents.BURNED_MEAT)));
    public static final Item TANGLED_WEB = registerItem("tangled_web", new Item(new Item.Settings()));
    public static final Item WEB_UNTANGLING = registerItem("web_untangling",
            new WebUntanglingItem(new Item.Settings().maxDamage(ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE))
    );
    public static final Item CHEVAL = registerItem("cheval", new Item(new Item.Settings().food(ModFoodComponents.CHEVAL)));
    public static final Item COOKED_CHEVAL = registerItem("cooked_cheval", new Item(new Item.Settings().food(ModFoodComponents.COOKED_CHEVAL)));
    public static final Item BAT_WING = registerItem("bat_wing", new Item(new Item.Settings().food(ModFoodComponents.BAT_WING)));
    public static final Item WITCH_WART = registerItem("witch_wart", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(AnimageddonMod.MOD_ID, name), item);
    }

    public static void register() {
        AnimageddonMod.LOGGER.info("Registering Mod Items for " + AnimageddonMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries ->
        {
            entries.addBefore(Items.ROTTEN_FLESH, CURED_MEAT, BURNED_MEAT);
            entries.addAfter(Items.RABBIT_STEW, CHICKEN_FEED);
            entries.addAfter(Items.COOKED_BEEF, CHEVAL);
            entries.addAfter(CHEVAL, COOKED_CHEVAL);
            entries.add(BAT_WING);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->
        {
            entries.addAfter(Items.GUNPOWDER, NITRE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.BONE_MEAL, TANGLED_WEB);
        });
    }

}