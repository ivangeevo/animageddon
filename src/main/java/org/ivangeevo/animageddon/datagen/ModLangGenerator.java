package org.ivangeevo.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.animageddon.block.ModBlocks;
import org.ivangeevo.animageddon.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLangGenerator extends FabricLanguageProvider {

    public ModLangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.CHICKEN_FEED, "Chicken Feed");
        translationBuilder.add(ModItems.NITRE, "Nitre");
        translationBuilder.add(ModItems.CURED_MEAT, "Cured Meat");
        translationBuilder.add(ModItems.BURNED_MEAT, "Burned Meat");
        translationBuilder.add(ModBlocks.WEB_BLOCK, "Partially Broken Cobweb");
    }

}