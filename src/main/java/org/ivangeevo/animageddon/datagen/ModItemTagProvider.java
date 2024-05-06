package org.ivangeevo.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
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
        getOrCreateTagBuilder(ModTags.Items.SEED_ITEMS)
                .add(Items.WHEAT_SEEDS)
                .add(Items.MELON_SEEDS)
                .add(Items.PUMPKIN_SEEDS)
                .add(Items.BEETROOT_SEEDS)
                .add(Items.TORCHFLOWER_SEEDS);

    }
    private void addToConventionalTags()
    {
        getOrCreateTagBuilder(BTWRConventionalTags.Items.CHICKEN_TEMPT_ITEMS)
                .add(ModItems.CHICKEN_FEED);

    }

}