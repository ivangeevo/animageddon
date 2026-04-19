package org.btwr.animageddon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.animageddon.entity.ModEntities;
import org.btwr.animageddon.item.ModComponents;
import org.btwr.animageddon.models.SpiderWebEntityRenderer;
import org.btwr.animageddon.render.entity.JungleSpiderEntityRenderer;

public class AnimageddonModClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_JUNGLE_SPIDER_LAYER = new EntityModelLayer(
            Identifier.of(AnimageddonMod.MOD_ID, "jungle_spider"), "main"
    );

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WEB_BLOCK, RenderLayer.getCutout());

        ModelPredicateProviderRegistry.register(Items.FISHING_ROD, Identifier.of(AnimageddonMod.MOD_ID, "has_bait"),
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (itemStack == null) {
                        return 0.0F;
                    }

                    // Retrieve the attachment value
                    Boolean hasBait = itemStack.get(ModComponents.HAS_BAIT_COMPONENT);

                    // Return 1.0F if baited, otherwise 0.0F
                    return hasBait != null && hasBait ? 1.0F : 0.0F;
                }
        );

        EntityRendererRegistry.register(ModEntities.JUNGLE_SPIDER, JungleSpiderEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_JUNGLE_SPIDER_LAYER, SpiderEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.SPIDER_WEB, SpiderWebEntityRenderer::new);

    }

}