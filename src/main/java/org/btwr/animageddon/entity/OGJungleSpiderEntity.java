package org.btwr.animageddon.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class OGJungleSpiderEntity extends SpiderEntity {

    public OGJungleSpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
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
        this.goalSelector.add(5, new LeafWanderGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.add(3, new TargetGoal<>(this, IronGolemEntity.class));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) return;

        BlockPos below = this.getBlockPos().down();
        boolean onLeaves = this.getWorld().getBlockState(below).isIn(BlockTags.LEAVES);

        if (onLeaves) {
            LivingEntity target = this.getTarget();
            String runningGoals = this.goalSelector.getGoals().stream()
                    .filter(PrioritizedGoal::isRunning)
                    .map(g -> g.getGoal().getClass().getSimpleName())
                    .toList()
                    .toString();

            System.out.println("[JungleSpider] onLeaves=true"
                    + " hasTarget=" + (target != null)
                    + " navIdle=" + this.getNavigation().isIdle()
                    + " runningGoals=" + runningGoals
                    + " pos=" + this.getBlockPos()
                    + " stableLeaf=" + isStableLeafDebug(below)
            );
        }
    }

    private boolean isStableLeafDebug(BlockPos pos) {
        int count = 0;
        World w = this.getWorld();
        if (w.getBlockState(pos.north()).isIn(BlockTags.LEAVES)) count++;
        if (w.getBlockState(pos.south()).isIn(BlockTags.LEAVES)) count++;
        if (w.getBlockState(pos.east()).isIn(BlockTags.LEAVES)) count++;
        if (w.getBlockState(pos.west()).isIn(BlockTags.LEAVES)) count++;
        return count >= 3;
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

    public static boolean canSpawn(EntityType<OGJungleSpiderEntity> type,
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

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        // Strongly prefer leaf canopy positions
        if (world.getBlockState(pos.down()).isIn(BlockTags.LEAVES)) {
            return 10.0F;
        }
        // Penalize ground-level positions heavily so the spider
        // stays on the canopy rather than trying to path down to targets
        if (world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
            return -10.0F;
        }
        return super.getPathfindingFavor(pos, world);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world) {
            @Override
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new JungleSpiderNodeMaker();
                this.nodeMaker.setCanEnterOpenDoors(true);
                this.nodeMaker.setCanSwim(true);
                return new PathNodeNavigator(this.nodeMaker, range);
            }
        };
    }

    public static class JungleSpiderNodeMaker extends LandPathNodeMaker {

        @Override
        protected double getFeetY(BlockPos pos) {
            BlockState state = this.context.getBlockState(pos.down());
            if (state.isIn(BlockTags.LEAVES)) {
                return pos.getY();
            }
            return super.getFeetY(pos);
        }

        @Override
        public PathNode getStart() {
            PathNode start = super.getStart();
            if (start != null) {
                BlockPos blockPos = new BlockPos(start.x, start.y - 1, start.z);
                if (this.context.getBlockState(blockPos).isIn(BlockTags.LEAVES)) {
                    start.type = PathNodeType.WALKABLE;
                    start.penalty = 0.0F;
                }
            }
            return start;
        }

        @Override
        public PathNode getNode(BlockPos pos) {
            PathNode node = super.getNode(pos);
            if (node != null) {
                BlockPos blockPos = new BlockPos(node.x, node.y - 1, node.z);
                if (this.context.getBlockState(blockPos).isIn(BlockTags.LEAVES)) {
                    node.type = PathNodeType.WALKABLE;
                    node.penalty = 0.0F;
                }
            }
            return node;
        }

        @Override
        @Nullable
        protected PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY,
                                       Direction direction, PathNodeType nodeType)
        {

            BlockPos floorPos = new BlockPos(x, y - 1, z);
            boolean onLeaves = this.context.getBlockState(floorPos).isIn(BlockTags.LEAVES);

            if (onLeaves) {
                // Reject unstable edge leaves before ever creating a node
                if (!isStableLeaf(this.context, x, y - 1, z)) {
                    return null;
                }

                PathNodeType type = PathNodeType.WALKABLE;
                float penalty = this.entity.getPathfindingPenalty(type);
                if (penalty >= 0.0F) {
                    PathNode node = this.getNode(x, y, z);
                    node.type = type;
                    node.penalty = penalty;
                    return node;
                }
                return null;
            }

            return super.getPathNode(x, y, z, maxYStep, prevFeetY, direction, nodeType);
        }

        @Override
        public PathNodeType getNodeType(PathContext context, int x, int y, int z, MobEntity mob) {
            PathNodeType type = super.getNodeType(context, x, y, z, mob);
            if (type == PathNodeType.LEAVES) {
                return isStableLeaf(context, x, y, z) ? PathNodeType.WALKABLE : PathNodeType.BLOCKED;
            }
            return type;
        }

        @Override
        public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
            PathNodeType type = super.getDefaultNodeType(context, x, y, z);
            if (type == PathNodeType.LEAVES) {
                return isStableLeaf(context, x, y, z) ? PathNodeType.WALKABLE : PathNodeType.BLOCKED;
            }
            return type;
        }

        private boolean isStableLeaf(PathContext context, int x, int y, int z) {
            int count = 0;
            // Check horizontal neighbors at the SAME y level
            if (context.getBlockState(new BlockPos(x + 1, y, z)).isIn(BlockTags.LEAVES)) count++;
            if (context.getBlockState(new BlockPos(x - 1, y, z)).isIn(BlockTags.LEAVES)) count++;
            if (context.getBlockState(new BlockPos(x, y, z + 1)).isIn(BlockTags.LEAVES)) count++;
            if (context.getBlockState(new BlockPos(x, y, z - 1)).isIn(BlockTags.LEAVES)) count++;
            return count >= 3;
        }
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

    static class LeafWanderGoal extends WanderAroundFarGoal {
        public LeafWanderGoal(OGJungleSpiderEntity spider, double speed) {
            super(spider, speed);
        }

        @Nullable
        @Override
        protected Vec3d getWanderTarget() {
            // Get a candidate target from the parent
            Vec3d candidate = super.getWanderTarget();
            if (candidate == null) return null;

            // Check if the target position has leaf support
            BlockPos targetPos = BlockPos.ofFloored(candidate).down();
            if (this.mob.getWorld().getBlockState(targetPos).isIn(BlockTags.LEAVES)) {
                return candidate;
            }

            // If no leaf support, try to find a nearby leaf-supported position
            for (int attempts = 0; attempts < 10; attempts++) {
                Vec3d fallback = super.getWanderTarget();
                if (fallback == null) continue;
                BlockPos fallbackFloor = BlockPos.ofFloored(fallback).down();
                if (this.mob.getWorld().getBlockState(fallbackFloor).isIn(BlockTags.LEAVES)) {
                    return fallback;
                }
            }

            // If we're currently on leaves, stay put rather than wander off
            BlockPos currentFloor = this.mob.getBlockPos().down();
            if (this.mob.getWorld().getBlockState(currentFloor).isIn(BlockTags.LEAVES)) {
                return null; // returning null cancels the wander goal for this cycle
            }

            return candidate; // not on leaves, allow normal ground wandering
        }
    }

    @Override
    public boolean isInAttackRange(LivingEntity target) {
        BlockPos below = this.getBlockPos().down();
        if (this.getWorld().getBlockState(below).isIn(BlockTags.LEAVES)) {
            // If target is more than 2 blocks stateBelow, consider them in range
            // so the attack goal stops trying to path down to them
            if (this.getY() - target.getY() > 2.0) {
                return true;
            }
        }
        return super.isInAttackRange(target);
    }

}
