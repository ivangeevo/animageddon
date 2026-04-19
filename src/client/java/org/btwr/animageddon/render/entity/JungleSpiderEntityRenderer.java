package org.btwr.animageddon.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;
import org.btwr.animageddon.entity.JungleSpiderEntity;

public class JungleSpiderEntityRenderer extends SpiderEntityRenderer<JungleSpiderEntity> {

    private static final Identifier TEXTURE = Identifier.of(AnimageddonMod.MOD_ID,"textures/entity/jungle_spider.png");
    private static final float SCALE = 0.7F;

    public JungleSpiderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.CAVE_SPIDER);
        this.shadowRadius *= SCALE;
    }

    @Override
    protected void scale(JungleSpiderEntity jungleSpiderEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public Identifier getTexture(JungleSpiderEntity jungleSpiderEntity) {
        return TEXTURE;
    }

}
