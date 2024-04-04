package org.animageddon.mixin;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
    @Shadow private boolean canBreakDoors;
    @Shadow public abstract boolean canBreakDoors();


    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world, boolean canBreakDoors) {
        super(entityType, world);
        this.canBreakDoors = canBreakDoors;
    }


    @Inject(method = "initGoals", at = @At("HEAD"), cancellable = true)
    private void setInitGoals(CallbackInfo ci) {

        this.goalSelector.add(2, new ZombieAttackGoal((ZombieEntity)(Object) this, 1.0, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal((ZombieEntity)(Object)this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal((ZombieEntity)(Object)this, 1.0));
        this.targetSelector.add(1, new RevengeGoal((ZombieEntity)(Object)this, new Class[0]).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity) this, PlayerEntity.class, true));

        // Added goals for attacking animals
        this.targetSelector.add(2, new ActiveTargetGoal<SheepEntity>((MobEntity) this, SheepEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<CowEntity>((MobEntity) this, CowEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<PigEntity>((MobEntity) this, PigEntity.class, true));

        //this.targetSelector.add(1, new EatMeatGoal((ZombieEntity)(Object)this));

        this.targetSelector.add(4, new ActiveTargetGoal<MerchantEntity>((MobEntity) this, MerchantEntity.class, false));
        this.targetSelector.add(4, new ActiveTargetGoal<IronGolemEntity>((MobEntity) this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<TurtleEntity>((ZombieEntity)(Object)this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    ci.cancel();

    }



}
