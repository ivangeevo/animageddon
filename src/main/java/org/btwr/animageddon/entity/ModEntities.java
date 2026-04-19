package org.btwr.animageddon.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;
import org.btwr.animageddon.entity.projectile.SpiderWebEntity;

public class ModEntities {

    public static final EntityType<JungleSpiderEntity> JUNGLE_SPIDER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AnimageddonMod.MOD_ID, "jungle_spider"),
            EntityType.Builder.create(JungleSpiderEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.7f, 0.5f)
                    .build()
    );

    public static final EntityType<SpiderWebEntity> SPIDER_WEB = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AnimageddonMod.MOD_ID, "spider_web"),
            EntityType.Builder.create((EntityType.EntityFactory<SpiderWebEntity>)SpiderWebEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build("spider_web")
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(JUNGLE_SPIDER, JungleSpiderEntity.createJungleSpiderAttributes());
    }
}
