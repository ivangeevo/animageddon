package org.btwr.animageddon.util;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.btwr.animageddon.entity.animal_kicking.KickAccess;
import org.btwr.animageddon.entity.animal_kicking.KickState;

public final class KickPose {
    private static final float MAX_ANGLE = 1.4f; // positive pitch swings a hind foot backwards

    /** animationProgress is entity.age + partialTick, as passed to setAngles. */
    public static void apply(Entity entity, ModelPart leftHind, ModelPart rightHind, float animationProgress, boolean bothLegs) {
        if (!(entity instanceof KickAccess access)) return;
        KickState state = access.animageddon$getKickState();
        if (state == null || !state.active()) return;

        float partialTick = animationProgress - entity.age;
        float t = MathHelper.clamp((state.progress + partialTick) / KickState.DURATION, 0f, 1f);
        float angle = MathHelper.sin(t * MathHelper.PI) * MAX_ANGLE; // eases out and back

        if (bothLegs) {
            leftHind.pitch = angle;
            rightHind.pitch = angle;
        } else {
            (state.leg == 0 ? leftHind : rightHind).pitch = angle;
        }
    }

    private KickPose() {}
}