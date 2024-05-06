package org.ivangeevo.animageddon;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.ivangeevo.animageddon.datagen.ModBlockTagProvider;
import org.ivangeevo.animageddon.datagen.ModItemTagProvider;
import org.ivangeevo.animageddon.datagen.ModRecipeProvider;

public class AnimageddonDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        //pack.addProvider(ModModelGenerator::new);

    }

}
