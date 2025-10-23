package org.ivangeevo.animageddon.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.data.attachments.hunger.AnimalHungerAttachedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin {

    @Inject(method = "create(" +
            "Lnet/minecraft/server/world/ServerWorld;Ljava/util/function/Consumer;Lnet/minecraft/util/math/BlockPos;" +
            "Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;)Lnet/minecraft/entity/EntityData;"
            )
    )
    private <T> void onCreate(ServerWorld world, @Nullable Consumer<T> afterConsumer, BlockPos pos, SpawnReason reason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<T> cir, @Local MobEntity mobEntity) {
        if (mobEntity instanceof AnimalEntity animal && animal.hasAttached(ModDataAttachments.ANIMAL_HUNGER_DATA)) {
            AnimalHungerAttachedData data = animal.getAttached(ModDataAttachments.ANIMAL_HUNGER_DATA);
            assert data != null;
            data.initHungerWithVariance(animal);
            animal.setAttached(ModDataAttachments.ANIMAL_HUNGER_DATA, data);
        }
    }


}
