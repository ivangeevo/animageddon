package org.ivangeevo.animageddon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.ivangeevo.animageddon.block.ModBlocks;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.event.ModEntityLootTableEvents;
import org.ivangeevo.animageddon.event.ModEntityUseEvents;
import org.ivangeevo.animageddon.item.ModComponents;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.util.ServerTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimageddonMod implements ModInitializer
{

    public static final String MOD_ID = "animageddon";
    public static final Logger LOGGER = LoggerFactory.getLogger("animageddon");


    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocksAndAddToGroups();
        ModItems.registerModItemsAndAddToGroups();
        ModComponents.register();
        ModDataAttachments.register();
        ModDamageTypes.register();

        ModEntityLootTableEvents.register();
        //ModPlayerBlockBreakEvents.register();

        // Initialize the helper class for keeping track of current time of day on the server world only
        ServerLifecycleEvents.SERVER_STARTING.register(ServerTimeHelper::setServerInstance);

        ModEntityUseEvents.register();
    }
}
