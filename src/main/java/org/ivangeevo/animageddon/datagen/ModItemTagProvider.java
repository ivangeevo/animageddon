package org.ivangeevo.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.tag.BTWRConventionalTags;
import org.ivangeevo.animageddon.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        addToModTags();
        addToConventionalTags();
    }

    private void addToModTags()
    {
        getOrCreateTagBuilder(ModTags.Items.SEEDS_FOR_CHICKEN)
                .add(Items.WHEAT_SEEDS)
                .add(Items.MELON_SEEDS)
                .add(Items.PUMPKIN_SEEDS)
                .add(Items.BEETROOT_SEEDS);

    }
    private void addToConventionalTags()
    {
        getOrCreateTagBuilder(BTWRConventionalTags.Items.CHICKEN_TEMPT_ITEMS)
                .addTag(ModTags.Items.SEEDS_FOR_CHICKEN);

    }

}