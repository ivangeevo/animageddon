package org.btwr.animageddon.event;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
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
import org.btwr.animageddon.data.loot.SpiderWebCondition;
import org.btwr.animageddon.item.ModItems;
import org.btwr.animageddon.mixin.item.ItemEntryAccessor;
import org.btwr.animageddon.mixin.LootPoolBuilderAccessor;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.entity.EntityType.*;

public class ModEntityLootTableEvents {

    // Register loot table changes
    public static void register() {
        addItemToLootTableWithSmelt(HORSE.getLootTableId(), ModItems.CHEVAL, ModItems.COOKED_CHEVAL, 1.0f, 3.0f);
        addItemToLootTableWithSmelt(DONKEY.getLootTableId(), ModItems.CHEVAL, ModItems.COOKED_CHEVAL, 1.0f, 3.0f);
        addItemToLootTableWithSmelt(MULE.getLootTableId(), ModItems.CHEVAL, ModItems.COOKED_CHEVAL, 1.0f, 3.0f);

        modifySpecificItem(CREEPER.getLootTableId(), Items.GUNPOWDER, ModItems.NITRE);

        // TODO: Make it so that when the mob dies on fire it drops burned meat instead of cooked.
        //modifyFoodItemToBurned(COW.getLootTableId(), Items.COOKED_BEEF, ModItems.BURNED_MEAT,1);

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

        // spiders cobweb loot table modification
        modifySpiderString();
    }

    private static void addItemToLootTable(RegistryKey<LootTable> registryKey, Item toAdd, float min, float max) {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (registryKey != key) return;

            tableBuilder.pool(net.minecraft.loot.LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(toAdd)
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max))))
            );
        });
    }

    private static void addItemToLootTableWithSmelt(RegistryKey<LootTable> registryKey, Item rawItem, Item cookedItem, float min, float max) {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (registryKey != key) return;

            // Raw drop when not on fire
            tableBuilder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(rawItem)
                            .conditionally(InvertedLootCondition.builder(createSmeltLootCondition(registries)))
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max))))
            );

            // Cooked drop when on fire
            tableBuilder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(cookedItem)
                            .conditionally(createSmeltLootCondition(registries))
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max))))
            );
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

    private static void modifyFoodItemToBurned(RegistryKey<LootTable> registryKey, Item target, Item toReplace, int count) {
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

                    if (builder.conditionally(createSmeltLootCondition(registries)) != null) {
                        return entry;
                    }

                    return ItemEntry.builder(toReplace)
                            .conditionally(createSmeltLootCondition(registries))
                            .apply(() -> SetCountLootFunction.builder(ConstantLootNumberProvider.create(count)).build())
                            .build();
                });

                ((LootPoolBuilderAccessor) builder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(l));

            });
        });
    }

    private static void modifySpiderString() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            // Check if the key is for target's loot table
            if (SPIDER.getLootTableId() != key) return;

            tableBuilder.modifyPools(builder -> {
                List<LootPoolEntry> l = new ArrayList<>(((LootPoolBuilderAccessor) builder).getEntries().build());
                l.replaceAll(entry -> {
                    if (!(entry instanceof ItemEntry itemEntry)) return entry;

                    if (((ItemEntryAccessor) itemEntry).getItem().value() != Items.STRING)
                        return entry;

                   return ItemEntry.builder(Items.STRING)
                           .conditionally(SpiderWebCondition.builder())
                           .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
                           .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0.0F, 1.0F))).build();
                });

                ((LootPoolBuilderAccessor) builder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(l));
            });
        });
    }

    protected static AnyOfLootCondition.Builder createSmeltLootCondition(RegistryWrapper.WrapperLookup registryLookup) {
        RegistryWrapper.Impl<Enchantment> impl = registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return AnyOfLootCondition.builder(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true))), EntityPropertiesLootCondition.builder(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().mainhand(ItemPredicate.Builder.create().subPredicate(ItemSubPredicateTypes.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(EnchantmentTags.SMELTS_LOOT), NumberRange.IntRange.ANY))))))));
    }

}