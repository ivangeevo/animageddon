package org.ivangeevo.animageddon.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.ivangeevo.animageddon.block.ModBlocks;

public class AnimageddonModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WEB_BLOCK, RenderLayer.getCutout());

    }
}
