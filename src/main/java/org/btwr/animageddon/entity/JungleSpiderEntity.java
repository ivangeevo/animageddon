package org.btwr.animageddon.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class JungleSpiderEntity extends SpiderEntity {

    public JungleSpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.LEAVES, 0.0F);
    }

    public static DefaultAttributeContainer.Builder createJungleSpiderAttributes() {
        return SpiderEntity.createSpiderAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, ArmadilloEntity.class, 6.0F, 1.0, 1.2, entity -> !((ArmadilloEntity)entity).isNotIdle()));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(4, new AttackGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new SpiderEntity.TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.add(3, new SpiderEntity.TargetGoal<>(this, IronGolemEntity.class));
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (super.tryAttack(target)) {
            if (target instanceof LivingEntity) {
                int poisonDurationInSeconds = 0;
                if (this.getWorld().getDifficulty() == Difficulty.NORMAL) {
                    poisonDurationInSeconds = 7;
                } else if (this.getWorld().getDifficulty() == Difficulty.HARD) {
                    poisonDurationInSeconds = 15;
                }

                if (poisonDurationInSeconds > 0) {
                    ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, poisonDurationInSeconds * 20, 0), this);

                    // adjust for btw difficulties later on
                    int hungerDuration = 30;
                    ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, hungerDuration * 20, 0));
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        return entityData;
    }

    @Override
    public Vec3d getVehicleAttachmentPos(Entity vehicle) {
        return vehicle.getWidth() <= this.getWidth() ? new Vec3d(0.0, 0.21875 * (double)this.getScale(), 0.0) : super.getVehicleAttachmentPos(vehicle);
    }

    public static boolean canSpawn(EntityType<JungleSpiderEntity> type,
                                   ServerWorldAccess world,
                                   SpawnReason spawnReason,
                                   BlockPos pos,
                                   Random random) {

        if (!world.getBiome(pos).isIn(BiomeTags.IS_JUNGLE)) {
            return false;
        }

        // allow normal ground spawning
        BlockState stateBelow = world.getBlockState(pos.down());

        boolean onLeaves = stateBelow.isIn(BlockTags.LEAVES);

        boolean validGround = stateBelow.isSolidBlock(world, pos.down());

        if (!onLeaves /**&& !validGround**/) {
            return false;
        }

        return world.isSpaceEmpty(type.getSpawnBox(pos.getX(), pos.getY(), pos.getZ()));
    }

    // Attack goal without the brightness check from the normal spiders
    static class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(SpiderEntity spider) {
            super(spider, 1.0, true);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.mob.hasPassengers();
        }

        @Override
        public boolean shouldContinue() {
            if (this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            }
            return super.shouldContinue();
        }

        private boolean isOnLeavesAboveTarget(LivingEntity target) {
            BlockPos below = this.mob.getBlockPos().down();
            return this.mob.getWorld().getBlockState(below).isIn(BlockTags.LEAVES)
                    && this.mob.getY() - target.getY() > 2.0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null && isOnLeavesAboveTarget(target)) {
                // Don't try to path down to the target — just look at them
                // and wait, mimicking BTW's canSpiderCollideWithLeavesBlock
                // "drop down" logic which required target.posY > blockY
                this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);
                return;
            }
            super.tick();
        }
    }

}
