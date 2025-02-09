package org.ivangeevo.animageddon.datagen;

import btwr.btwr_sl.tag.BTWRConventionalTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        addToVanillaTags();
        addToConventionalTags();
        addToModTags();
    }

    private void addToVanillaTags() {
        getOrCreateTagBuilder(ItemTags.COW_FOOD)
                .add(Items.SHORT_GRASS);

        getOrCreateTagBuilder(ItemTags.SHEEP_FOOD)
                .add(Items.SHORT_GRASS);




    }

    private void addToModTags() {
        getOrCreateTagBuilder(ModTags.Items.SEEDS_FOR_CHICKEN)
                .add(Items.WHEAT_SEEDS)
                .add(Items.MELON_SEEDS)
                .add(Items.PUMPKIN_SEEDS)
                .add(Items.BEETROOT_SEEDS);

        getOrCreateTagBuilder(ModTags.Items.PIG_BREEDING_ITEMS)
                .addOptional(Identifier.of("vegehenna", "chocolate"));

    }

    private void addToConventionalTags() {
        getOrCreateTagBuilder(BTWRConventionalTags.Items.CHICKEN_TEMPT_ITEMS)
                .addTag(ModTags.Items.SEEDS_FOR_CHICKEN);
    }

}