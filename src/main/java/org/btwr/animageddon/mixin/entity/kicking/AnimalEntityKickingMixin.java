package org.btwr.animageddon.mixin.entity.kicking;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.btwr.animageddon.entity.animal_kicking.KickAccess;
import org.btwr.animageddon.entity.animal_kicking.KickLogic;
import org.btwr.animageddon.entity.animal_kicking.KickState;
import org.btwr.animageddon.entity.ai.FleeFromAttackerGoal;
import org.btwr.animageddon.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityKickingMixin extends PassiveEntity implements KickAccess {

    // lazily allocated variable for handling client side animation
    @Unique
    private KickState animageddon$kickState;

    @Unique private long animageddon$lastHurtTime = -1L;
    @Unique private LivingEntity animageddon$lastAttacker;

    protected AnimalEntityKickingMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public KickState animageddon$getKickState() {
        return animageddon$kickState;
    }

    @Override
    public KickState animageddon$getOrCreateKickState() {
        if (animageddon$kickState == null) {
            animageddon$kickState = new KickState();
        }
        return animageddon$kickState;
    }

    @Override
    public long animageddon$getLastHurtTime() {
        return animageddon$lastHurtTime;
    }

    @Override
    public LivingEntity animageddon$getLastAttacker() {
        return animageddon$lastAttacker;
    }

    @Override
    public void animageddon$markHurt(long worldTime, LivingEntity attacker) {
        animageddon$lastHurtTime = worldTime;
        animageddon$lastAttacker = attacker;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void kicking$swapPanicGoal(EntityType<? extends AnimalEntity> type, World world, CallbackInfo ci) {
        if (world.isClient || !type.isIn(ModTags.EntityTypes.KICKING_ANIMALS)) return;
        AnimalEntity self = (AnimalEntity) (Object) this;

        goalSelector.getGoals().removeIf(g -> g.getGoal() instanceof EscapeDangerGoal);
        goalSelector.add(1, new FleeFromAttackerGoal(self, self instanceof AbstractHorseEntity ? 1.2 : 1.9));
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickKicking(CallbackInfo ci) {
        AnimalEntity self = (AnimalEntity) (Object) this;

        if (self.getWorld().isClient) {
            if (animageddon$kickState != null) {
                animageddon$kickState.tick();
            }
        } else if (self.getWorld() instanceof ServerWorld serverWorld) {
            KickLogic.serverTick(self, serverWorld);
        }
    }

    @Inject(method = "handleStatus", at = @At("HEAD"), cancellable = true)
    private void handleKickStatus(byte status, CallbackInfo ci) {
        if (status == KickLogic.STATUS_KICK_LEFT || status == KickLogic.STATUS_KICK_RIGHT) {
            animageddon$getOrCreateKickState().start(status == KickLogic.STATUS_KICK_RIGHT ? 1 : 0);
            ci.cancel();
        }
    }
}
