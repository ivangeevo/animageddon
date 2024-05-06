package org.ivangeevo.animageddon;

import net.fabricmc.api.ModInitializer;
import org.ivangeevo.animageddon.item.AnimaggedonModItemGroup;
import org.ivangeevo.animageddon.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimageddonMod implements ModInitializer
{

    public static final String MOD_ID = "animageddon";

    public static final String MOD_VERSION = "1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("animageddon");


    @Override
    public void onInitialize()
    {
        AnimaggedonModItemGroup.registerItemGroups();
        //ModBlock.registerModBlocks();
        ModItems.registerModItems();

    }
}
