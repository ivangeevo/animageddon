package org.btwr.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.animageddon.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLangGenerator extends FabricLanguageProvider {

    public ModLangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        this.generateBlockTranslations(translationBuilder);
        this.generateItemTranslations(translationBuilder);
    }

    private void generateBlockTranslations(TranslationBuilder tb) {
        tb.add(ModBlocks.WEB_BLOCK, "Partially Broken Cobweb");
    }

    private void generateItemTranslations(TranslationBuilder tb) {
        tb.add(ModItems.CHICKEN_FEED, "Chicken Feed");
        tb.add(ModItems.NITRE, "Nitre");
        tb.add(ModItems.CURED_MEAT, "Cured Meat");
        tb.add(ModItems.BURNED_MEAT, "Burned Meat");
        tb.add(ModItems.TANGLED_WEB, "Tangled Web");
        tb.add(ModItems.WEB_UNTANGLING, "Web Untangling");
        tb.add(ModItems.CHEVAL, "Raw Cheval");
        tb.add(ModItems.COOKED_CHEVAL, "Cooked Cheval");
    }

}