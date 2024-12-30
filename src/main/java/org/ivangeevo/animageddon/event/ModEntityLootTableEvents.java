package org.ivangeevo.animageddon.event;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.mixin.ItemEntryAccessor;
import org.ivangeevo.animageddon.mixin.LootPoolBuilderAccessor;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.entity.EntityType.*;

public class ModEntityLootTableEvents
{
    // Register loot table changes
    public static void initialize() {
        modifySpecificItemWithCount(CREEPER.getLootTableId(), Items.GUNPOWDER, ModItems.NITRE, 1);

        /**
        // Burned meat entries
        modifySpecificItemWithOnFireCondition(PIG.getLootTableId(), Items.COOKED_PORKCHOP, ModItems.BURNED_MEAT);
        modifySpecificItemWithOnFireCondition(COW.getLootTableId(), Items.COOKED_BEEF, ModItems.BURNED_MEAT);
        modifySpecificItemWithOnFireCondition(SHEEP.getLootTableId(), Items.COOKED_MUTTON, ModItems.BURNED_MEAT);
        modifySpecificItemWithOnFireCondition(CHICKEN.getLootTableId(), Items.COOKED_CHICKEN, ModItems.BURNED_MEAT);
        modifySpecificItemWithOnFireCondition(RABBIT.getLootTableId(), Items.COOKED_RABBIT, ModItems.BURNED_MEAT);
        modifySpecificItemWithOnFireCondition(COD.getLootTableId(), Items.COOKED_COD, ModItems.BURNED_MEAT);
        modifySpecificItemWithOnFireCondition(SALMON.getLootTableId(), Items.COOKED_SALMON, ModItems.BURNED_MEAT);
         **/


        // TODO: add meats?:
        // dolphin
        // donkey
        // fox
        // goat
        // horse
        // llama
        // mule
        // panda
        // polar bear
        // trader llama
        // turtle
        // wolf

    }

    private static void modifySpecificItem(RegistryKey<LootTable> registryKey, Item target, Item toReplace) {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            // Check if the key is for target's loot table
            if (registryKey != key) return;

            tableBuilder.modifyPools(builder -> {
                List<LootPoolEntry> l = new ArrayList<>(((LootPoolBuilderAccessor) builder).getEntries().build());
                l.replaceAll(entry -> {
                    if (!(entry instanceof ItemEntry itemEntry))
                        return entry;
                    if (((ItemEntryAccessor) itemEntry).getItem().value() != target)
                        return entry;
                    ((ItemEntryAccessor) entry).setItem(Registries.ITEM.getEntry(toReplace));
                    return entry;
                });

                ((LootPoolBuilderAccessor) builder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(l));
            });
        });
    }

    private static void modifySpecificItemWithCount(RegistryKey<LootTable> registryKey, Item target, Item toReplace, int count) {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            // Check if the key is for target's loot table
            if (registryKey != key) return;

            tableBuilder.modifyPools(builder -> {
                List<LootPoolEntry> l = new ArrayList<>(((LootPoolBuilderAccessor) builder).getEntries().build());
                l.replaceAll(entry -> {
                    if (!(entry instanceof ItemEntry itemEntry))
                        return entry;
                    if (((ItemEntryAccessor) itemEntry).getItem().value() != target)
                        return entry;

                    // Replace the item and add a SetCount function to modify the count
                    return ItemEntry.builder(toReplace)
                            .apply(() -> SetCountLootFunction.builder(ConstantLootNumberProvider.create(count)).build())
                            .build();
                });

                ((LootPoolBuilderAccessor) builder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(l));
            });
        });
    }


}
