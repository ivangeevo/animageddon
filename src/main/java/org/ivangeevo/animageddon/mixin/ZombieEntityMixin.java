package org.ivangeevo.animageddon.mixin;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
    @Shadow private boolean canBreakDoors;
    @Shadow public abstract boolean canBreakDoors();


    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world, boolean canBreakDoors) {
        super(entityType, world);
        this.canBreakDoors = canBreakDoors;
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void onInitGoals(CallbackInfo ci)
    {
        // Added goals for attacking animals
        this.targetSelector.add(2, new ActiveTargetGoal<SheepEntity>((MobEntity) this, SheepEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<CowEntity>((MobEntity) this, CowEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<PigEntity>((MobEntity) this, PigEntity.class, true));
    }

}
