package org.btwr.animageddon.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin {

    @ModifyReturnValue(method = "canAttack", at = @At("RETURN"))
    private boolean allowSmallSlimesToAttack(boolean original) {
        // original = !isSmall() & canMoveVoluntarily()

        // If vanilla already allows it -> keep it
        if (original) return true;

        // Otherwise, re-evaluate without the size restriction
        SlimeEntity self = (SlimeEntity)(Object)this;
        return self.canMoveVoluntarily();
    }
}
