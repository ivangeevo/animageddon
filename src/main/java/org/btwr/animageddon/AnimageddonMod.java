package org.btwr.animageddon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.data.loot.ModLootConditions;
import org.btwr.animageddon.entity.ModEntities;
import org.btwr.animageddon.event.ModEntityLootTableEvents;
import org.btwr.animageddon.event.ModEntityUseEvents;
import org.btwr.animageddon.event.ModItemUseEvents;
import org.btwr.animageddon.item.ModComponents;
import org.btwr.animageddon.item.ModItems;
import org.btwr.animageddon.util.ServerTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimageddonMod implements ModInitializer {

    public static final String MOD_ID = "animageddon";
    public static final Logger LOGGER = LoggerFactory.getLogger("animageddon");

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModEntities.register();
        ModComponents.register();
        ModDataAttachments.register();
        ModLootConditions.register();

        ModItemUseEvents.register();
        ModEntityLootTableEvents.register();
        //ModPlayerBlockBreakEvents.register();

        // Initialize the helper class for keeping track of the current time of day on the server world only
        ServerLifecycleEvents.SERVER_STARTING.register(ServerTimeHelper::setServerInstance);

        ModEntityUseEvents.register();

        /**
        SpawnRestriction.register(
                ModEntities.JUNGLE_SPIDER,
                SpawnLocationTypes.UNRESTRICTED,
                Heightmap.Type.MOTION_BLOCKING,
                JungleSpiderEntity::canSpawn
        );

        BiomeModifications.addSpawn(
                context -> context.hasTag(BiomeTags.IS_JUNGLE),
                SpawnGroup.MONSTER,
                ModEntities.JUNGLE_SPIDER,
                80,
                1,
                1
        );
         **/
    }

}