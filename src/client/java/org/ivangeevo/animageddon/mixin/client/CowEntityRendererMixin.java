package org.ivangeevo.animageddon.mixin.client;

import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.data.attachments.hunger.AnimalHungerAttachedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntityRenderer.class)
public abstract class CowEntityRendererMixin {

    @Shadow @Final private static Identifier TEXTURE;

    @Unique
    private static final Identifier FAMISHED_TEXTURE = Identifier.of(
            AnimageddonMod.MOD_ID, "textures/entity/cow_famished.png"
    );

    @Unique
    private static final Identifier STARVING_TEXTURE = Identifier.of(
            AnimageddonMod.MOD_ID, "textures/entity/cow_starving.png"
    );

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/CowEntity;)Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"), cancellable = true)
    private void injectedGetTexture(CowEntity cowEntity, CallbackInfoReturnable<Identifier> cir) {
        var hungerData = cowEntity.getAttachedOrCreate(ModDataAttachments.ANIMAL_HUNGER_DATA);
        if (hungerData == null) return;
        cir.setReturnValue(hungerData.isFamished()
                ? FAMISHED_TEXTURE
                : hungerData.isStarving()
                ? STARVING_TEXTURE
                : TEXTURE
        );
    }
}
