package org.ivangeevo.animageddon.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityModelAdded;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuadrupedEntityModel.class)
public abstract class QuadrupedEntityModelMixin<T extends Entity> extends AnimalModel<T>
{




}