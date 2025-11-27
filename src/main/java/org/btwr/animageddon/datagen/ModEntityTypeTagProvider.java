package org.btwr.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.animageddon.tag.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public ModEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        getOrCreateTagBuilder(ModTags.EntityTypes.SUBJECT_TO_HUNGER_ANIMALS)
                .addTag(ModTags.EntityTypes.GRAZING_ANIMALS);

        getOrCreateTagBuilder(ModTags.EntityTypes.GRAZING_ANIMALS)
                .add(EntityType.COW);
    }

}