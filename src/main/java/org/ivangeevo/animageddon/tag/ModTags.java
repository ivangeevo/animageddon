package org.ivangeevo.animageddon.tag;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;

public class ModTags
{

    public static class Items
    {

        public static final TagKey<Item> CHICKEN_TEMPT_ITEMS = createTag("chicken_tempt_items");
        public static final TagKey<Item> PIG_BREEDING_ITEMS = createTag("pig_breeding_items");
        public static final TagKey<Item> SEEDS_FOR_CHICKEN = createTag("seeds_for_chicken");

        private static TagKey<Item> createTag (String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(AnimageddonMod.MOD_ID, name));
        }
    }
}
