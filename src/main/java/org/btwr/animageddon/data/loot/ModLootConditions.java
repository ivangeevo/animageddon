package org.btwr.animageddon.data.loot;

import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;

public class ModLootConditions {

    public static final LootConditionType SPIDER_WEB = Registry.register(
            Registries.LOOT_CONDITION_TYPE,
            Identifier.of(AnimageddonMod.MOD_ID, "spider_web"),
            new LootConditionType(SpiderWebCondition.CODEC)
    );

    public static void register() {}

}