package org.btwr.animageddon.entity.animal_kicking;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface KickAccess {
    @Nullable KickState animageddon$getKickState();
    KickState animageddon$getOrCreateKickState();
    long animageddon$getLastHurtTime(); // -1 if never hurt by a living attacker
    @Nullable LivingEntity animageddon$getLastAttacker();
    void animageddon$markHurt(long worldTime, LivingEntity attacker);
}