package org.ivangeevo.animageddon.event;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
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

public abstract class ModEntityLootTableEvents
{
    // Register loot table changes
    public static void initialize() {
        modifySpecificItem(CREEPER.getLootTableId(), Items.GUNPOWDER, ModItems.NITRE);

        // Burned meat entries
        modifyToBurnedMeat(CHICKEN.getLootTableId(), Items.COOKED_CHICKEN);
        modifyToBurnedMeat(COD.getLootTableId(), Items.COOKED_COD);
        modifyToBurnedMeat(COW.getLootTableId(), Items.COOKED_BEEF);
        // dolphin
        // donkey
        // fox
        // goat
        // horse
        // llama
        // mule
        // panda
        modifyToBurnedMeat(PIG.getLootTableId(), Items.COOKED_PORKCHOP);
        // polar bear
        modifyToBurnedMeat(RABBIT.getLootTableId(), Items.COOKED_RABBIT);
        modifyToBurnedMeat(SALMON.getLootTableId(), Items.COOKED_SALMON);
        modifyToBurnedMeat(SHEEP.getLootTableId(), Items.COOKED_MUTTON);
        // trader llama
        // turtle
        // wolf





    }

    private static void modifyToBurnedMeat(RegistryKey<LootTable> registryKey, Item target) {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            // Check if the key matches the target loot table
            if (registryKey != key) return;

            tableBuilder.modifyPools(builder -> {
                List<LootPoolEntry> entries = new ArrayList<>(((LootPoolBuilderAccessor) builder).getEntries().build());

                entries.replaceAll(entry -> {
                    // Ensure the entry is an ItemEntry
                    if (!(entry instanceof ItemEntry itemEntry)) return entry;

                    // Check if the entry matches the target item
                    if (((ItemEntryAccessor) itemEntry).getItem().value() != target) return entry;

                    // Get the Enchantment registry wrapper

                    // Create a new entry with the on-fire condition
                    return ItemEntry.builder(ModItems.BURNED_MEAT)
                            .conditionally(createOnFireOrSmeltLootCondition(registries))
                            .build();
                });

                // Replace the loot pool entries
                ((LootPoolBuilderAccessor) builder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(entries));
            });
        });
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

    // copied from vanilla with different name, so it doesn't override it
    protected static AnyOfLootCondition.Builder createOnFireOrSmeltLootCondition(RegistryWrapper. WrapperLookup registries) {
        RegistryWrapper<Enchantment> impl = registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return AnyOfLootCondition.builder(
                // If it's on fire naturally
                EntityPropertiesLootCondition.builder(
                        LootContext.EntityTarget.THIS,
                        EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true))
                ),
                // If it's killed by fire effect from an attacker
                EntityPropertiesLootCondition.builder(
                        LootContext.EntityTarget.DIRECT_ATTACKER,
                        EntityPredicate.Builder.create().equipment(
                                EntityEquipmentPredicate.Builder.create().mainhand(
                                        ItemPredicate.Builder.create().subPredicate(
                                                ItemSubPredicateTypes.ENCHANTMENTS,
                                                EnchantmentsPredicate.enchantments(List.of(
                                                        new EnchantmentPredicate(
                                                                impl.getOrThrow(EnchantmentTags.SMELTS_LOOT),
                                                                NumberRange.IntRange.ANY
                                                        )
                                                ))
                                        )
                                )
                        )
                )
        );
    }
}
