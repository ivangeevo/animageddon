package org.ivangeevo.animageddon.mixin.entity;


import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerEntityMixin extends HostileEntity {

    @Unique
    private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = (difficulty) -> difficulty == Difficulty.HARD;;
    @Unique
    private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this, DOOR_BREAK_DIFFICULTY_CHECKER);

    @Unique
    private boolean canBreakDoors;


    @Inject(method = "initialize", at = @At("TAIL"))
    private void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        float f = difficulty.getClampedLocalDifficulty();
        this.setCanBreakDoors(this.shouldBreakDoors() && random.nextFloat() < f * 0.1f);
        
        this.applyAttributeModifiers(f);

    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("CanBreakDoors", this.canBreakDoors());

    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void onReadCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setCanBreakDoors(nbt.getBoolean("CanBreakDoors"));

    }

    protected void applyAttributeModifiers(float chanceMultiplier) {
        //this.initAttributes();
        if (this.random.nextFloat() < chanceMultiplier * 0.05f) {
            this.setCanBreakDoors(this.shouldBreakDoors());
        }

    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        if (this.shouldBreakDoors() && NavigationConditions.hasMobNavigation(this)) {
            if (this.canBreakDoors != canBreakDoors) {
                this.canBreakDoors = canBreakDoors;
                ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
                if (canBreakDoors) {
                    this.goalSelector.add(1, this.breakDoorsGoal);
                } else {
                    this.goalSelector.remove(this.breakDoorsGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.remove(this.breakDoorsGoal);
            this.canBreakDoors = false;
        }
    }

    protected boolean shouldBreakDoors() {
        return true;
    }

    protected ZombieVillagerEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.targetSelector.add(2, new ActiveTargetGoal<SheepEntity>((MobEntity) this, SheepEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<CowEntity>((MobEntity) this, CowEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<PigEntity>((MobEntity) this, PigEntity.class, true));

    }

}