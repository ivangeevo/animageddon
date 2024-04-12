package org.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import org.animageddon.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {

        getOrCreateTagBuilder(ModTags.Items.SEED_ITEMS)
                .add(Items.WHEAT_SEEDS)
                .add(Items.MELON_SEEDS)
                .add(Items.PUMPKIN_SEEDS)
                .add(Items.BEETROOT_SEEDS)
                .add(Items.TORCHFLOWER_SEEDS);














    }
}