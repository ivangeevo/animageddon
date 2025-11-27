package org.btwr.animageddon.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;
import org.btwr.animageddon.data.attachments.hunger.LivingEntityHungerData;

public class ModTags {

    public static class Items {

        // Tempt items are only used for luring animals and cannot be fed to them
        public static final TagKey<Item> CHICKEN_TEMPT_ITEMS = createTag("chicken_tempt_items");
        public static final TagKey<Item> COW_TEMPT_ITEMS = createTag("cow_tempt_items");
        public static final TagKey<Item> PIG_TEMPT_ITEMS = createTag("pig_tempt_items");

        // Food items can be used to feed animals to decrease their hunger level or feed baby animals so they can grow up faster
        public static final TagKey<Item> CHICKEN_FOOD_ITEMS = createTag("chicken_food_items");
        public static final TagKey<Item> COW_FOOD_ITEMS = createTag("cow_food_items");
        public static final TagKey<Item> PIG_FOOD_ITEMS = createTag("pig_food_items");

        // Breeding items (Now this is probably not needed, because we consider breeding items to be only items likr
        // e.g ItemTags.COW_FOOD, ItemTags.CHICKEN_FOOD, etc..
        public static final TagKey<Item> PIG_BREEDING_ITEMS = createTag("pig_breeding_items");

        public static final TagKey<Item> SEEDS_FOR_CHICKEN = createTag("seeds_for_chicken");
        public static final TagKey<Item> FISH_BAITS = createTag("fish_baits");

        private static TagKey<Item> createTag (String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(AnimageddonMod.MOD_ID, name));
        }
    }

    public static class EntityTypes {
        /** Holds all animal entities that can be subject to hunger using the {@link LivingEntityHungerData} **/
        public static final TagKey<EntityType<?>> SUBJECT_TO_HUNGER_ANIMALS = createTag("subject_to_hunger_animals");

        public static final TagKey<EntityType<?>> GRAZING_ANIMALS = createTag("grazing_animals");

        private static TagKey<EntityType<?>> createTag (String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(AnimageddonMod.MOD_ID, name));
        }
    }

}