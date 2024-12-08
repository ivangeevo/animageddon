package org.ivangeevo.animageddon;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.ivangeevo.animageddon.datagen.ModBlockTagProvider;
import org.ivangeevo.animageddon.datagen.ModItemTagProvider;
import org.ivangeevo.animageddon.datagen.ModRecipeProvider;
import org.ivangeevo.animageddon.datagen.ModLangGenerator;

public class AnimageddonDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);

        // TODO: Figure out how to datagen an item group lang, aka how to get a
        //  registryKey for the custom item group created by the mod as it's just an item.
        //pack.addProvider(ModLangGenerator::new);

        //pack.addProvider(ModModelGenerator::new);

    }

}
