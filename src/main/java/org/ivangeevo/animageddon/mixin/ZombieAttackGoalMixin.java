package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieAttackGoal.class)
public abstract class ZombieAttackGoalMixin extends MeleeAttackGoal {
    @Shadow private int ticks;
    private final ZombieEntity zombie;

    public ZombieAttackGoalMixin(ZombieEntity zombie, double speed, boolean pauseWhenMobIdle) {
        super(zombie, speed, pauseWhenMobIdle);
        this.zombie = zombie;
    }

    @Inject(method = "start", at = @At("HEAD"))
    private void onStart(CallbackInfo ci) {
        if (this.mob.getAttacking() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) this.mob.getAttacking();

            // Check conditions for attacking players
            if (this.shouldAttackPlayer(player)) {
                // Initiate the attack
                this.customAttackingLogic(player);
            }
        }
    }

    // Add any additional helper methods or conditions as needed
    private boolean shouldAttackPlayer(PlayerEntity player) {
        // Add your custom conditions for attacking players
        return true; // Replace with your actual conditions
    }

    private void customAttackingLogic(PlayerEntity player) {
        // Your custom attack initiation logic
        this.ticks = 120 + this.mob.getRandom().nextInt(81); // Randomize between 120 and 200 ticks

        if (this.mob.getAttacking() instanceof SheepEntity || this.mob.getAttacking() instanceof CowEntity) {
            // Zombies prefer attacking Sheep & Cows 20% more than Pigs
            if (this.mob.getRandom().nextFloat() < 0.2f) {
                this.ticks -= 24; // Reduce the waiting time by 20%
            }
        }

        this.mob.handleAttack(player);
    }
}
