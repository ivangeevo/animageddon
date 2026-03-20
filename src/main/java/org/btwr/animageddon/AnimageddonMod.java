package org.btwr.animageddon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.entity.ModEntities;
import org.btwr.animageddon.event.ModEntityLootTableEvents;
import org.btwr.animageddon.event.ModEntityUseEvents;
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
        ModBlocks.registerModBlocksAndAddToGroups();
        ModItems.registerModItemsAndAddToGroups();
        ModEntities.register();
        ModComponents.register();
        ModDataAttachments.register();

        ModEntityLootTableEvents.register();
        //ModPlayerBlockBreakEvents.register();

        // Initialize the helper class for keeping track of current time of day on the server world only
        ServerLifecycleEvents.SERVER_STARTING.register(ServerTimeHelper::setServerInstance);

        ModEntityUseEvents.register();
    }

}