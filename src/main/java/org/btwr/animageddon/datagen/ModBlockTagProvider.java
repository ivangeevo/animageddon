package org.btwr.animageddon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.shared_library.tag.BTWRConventionalTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.FALL_DAMAGE_RESETTING)
                .add(ModBlocks.WEB_BLOCK);

        getOrCreateTagBuilder(BTWRConventionalTags.Blocks.WEB_BLOCKS)
                .add(ModBlocks.WEB_BLOCK);

        getOrCreateTagBuilder(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
                .addOptionalTag(BTWRConventionalTags.Blocks.WEB_BLOCKS);
    }

}