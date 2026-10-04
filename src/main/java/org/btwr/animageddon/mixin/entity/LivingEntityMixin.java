package org.btwr.animageddon.mixin.entity;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.CowEntity;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.entity.animal_kicking.KickAccess;
import org.btwr.shared_library.api.data.EntityAttachmentBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    //@Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void onEndBaseTick(CallbackInfo ci) {
        forLivingSubclass(CowEntity.class, cow -> {
            tickAndSync(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA, cow);
        });
    }

    /** Records who hurt a kicking animal **/
    @Inject(method = "damage", at = @At("RETURN"))
    private void animageddon$onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return; // damage was rejected (invulnerable, blocked, etc.)

        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getWorld().isClient) return;

        if ((Object) this instanceof KickAccess access && source.getAttacker() instanceof LivingEntity attacker) {
            access.animageddon$markHurt(self.getWorld().getTime(), attacker);
        }
    }

    @Unique
    private static <T extends Entity, A extends EntityAttachmentBase<T>> void tickAndSync(AttachmentType<A> type, T entity) {
        A attachment = entity.getAttachedOrCreate(type);
        attachment.tick(entity);
        if (attachment.isDirty()) entity.setAttached(type, attachment);
    }

    /** Helper method to instantiate subclasses of LivingEntity more easily **/
    @Unique
    @SuppressWarnings("unchecked")
    private <T extends LivingEntity> void forLivingSubclass(Class<T> type, Consumer<T> action) {
        if (type.isInstance(this)) {
            action.accept((T)(Object)this);
        }
    }

}