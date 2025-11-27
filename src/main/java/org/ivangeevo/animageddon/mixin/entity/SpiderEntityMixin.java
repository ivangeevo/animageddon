package org.ivangeevo.animageddon.mixin.entity;


import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.data.attachments.SpiderWebData;
import org.ivangeevo.animageddon.entity.ai.goal.NewCobwebShootGoal;
import org.ivangeevo.animageddon.entity.interfaces.SpiderEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SpiderEntity.class)
public abstract class SpiderEntityMixin extends HostileEntity implements SpiderEntityAdded {

    protected SpiderEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initDataTracker(EntityType entityType, World world, CallbackInfo ci) {
        injectFor(SpiderEntity.class, spider -> {
            spider.setAttached(ModDataAttachments.SPIDER_WEB_DATA, new SpiderWebData(false, SpiderWebData.TIME_BETWEEN_WEBS));
        });
    }

    @Inject(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 4))
    private void injectCobwebGoal(CallbackInfo ci) {
        injectFor(SpiderEntity.class, spider -> {
            this.goalSelector.add(2, new NewCobwebShootGoal(spider));
        });
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void injectedInitGoals(CallbackInfo ci) {
        injectFor(SpiderEntity.class, spider -> {
            this.targetSelector.add(2, new SpiderEntity.TargetGoal<>(spider, ChickenEntity.class));
            this.targetSelector.add(2, new SpiderEntity.TargetGoal<>(spider, RabbitEntity.class));
        });
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onEndTick(CallbackInfo ci) {
        injectFor(SpiderEntity.class, spider -> {
            var webData = spider.getAttached(ModDataAttachments.SPIDER_WEB_DATA);
            if (webData != null) {
                if (webData.timeToNextWeb() > 0) {
                    webData.decrementTimeToNextWeb();
                }
            }
        });
    }

    @Unique
    @SuppressWarnings("unchecked")
    private <T extends LivingEntity> void injectFor(Class<T> type, Consumer<T> action) {
        if (type.isInstance(this)) {
            action.accept((T) this);
        }
    }

}