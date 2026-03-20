package org.btwr.animageddon.data.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import org.btwr.animageddon.data.ModDataAttachments;

public record SpiderWebCondition() implements LootCondition {

    public static final MapCodec<SpiderWebCondition> CODEC =
            MapCodec.unit(new SpiderWebCondition());

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.get(LootContextParameters.THIS_ENTITY);

        if (entity instanceof SpiderEntity spider) {
            var data = spider.getAttached(ModDataAttachments.SPIDER_WEB_DATA);
            return data != null && data.hasWeb();
        }

        return false;
    }

    @Override
    public LootConditionType getType() {
        return ModLootConditions.SPIDER_WEB;
    }

    public static LootCondition.Builder builder() {
        return SpiderWebCondition::new;
    }
}