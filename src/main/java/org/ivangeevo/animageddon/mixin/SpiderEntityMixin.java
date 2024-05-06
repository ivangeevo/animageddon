package org.ivangeevo.animageddon.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.interfaces.SpiderEntityAdded;
import org.ivangeevo.animageddon.entity.projectile.CobwebEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpiderEntity.class)
public abstract class SpiderEntityMixin extends HostileEntity implements SpiderEntityAdded
{

    @Unique
    private static final TrackedData<Boolean> SHOOTING =
            DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Integer> TIME_TO_NEXT_WEB =
            DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int TIME_BETWEEN_WEBS = (20 * 60 * 20 ); // a full day

    protected int timeToNextWeb = 0;

    private int fireballStrength = 1;


    protected SpiderEntityMixin(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("HEAD"), cancellable = true)
    private void injectedInitGoals(CallbackInfo ci)
    {
        // Main goals
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(4, new AttackGoal((SpiderEntity)(Object)this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        // Target goals
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new SpiderEntity.TargetGoal<PlayerEntity>((SpiderEntity)(Object)this, PlayerEntity.class));
        this.targetSelector.add(3, new SpiderEntity.TargetGoal<IronGolemEntity>((SpiderEntity)(Object)this, IronGolemEntity.class));
        this.targetSelector.add(4, new SpiderEntity.TargetGoal<ChickenEntity>((SpiderEntity)(Object)this, ChickenEntity.class));
        this.targetSelector.add(4, new SpiderEntity.TargetGoal<RabbitEntity>((SpiderEntity)(Object)this, RabbitEntity.class));

        ci.cancel();
    }

    private void spitWeb(Entity targetEntity)
    {

        if ( !getWorld().isClient() )
        {
            Vec3d vec3d = this.getRotationVec(1.0F);

            double f = targetEntity.getX() - (this.getX() + vec3d.x * 4.0);
            double g = targetEntity.getBodyY(0.5) - (0.5 + this.getBodyY(0.5));
            double h = targetEntity.getZ() - (this.getZ() + vec3d.z * 4.0);

            CobwebEntity cobwebEntity = new CobwebEntity(getWorld(), this, f, g, h);

            getWorld().spawnEntity( cobwebEntity );

            timeToNextWeb = TIME_BETWEEN_WEBS;
        }
    }

    public boolean hasWeb()
    {
        return timeToNextWeb <= 0;
    }

    public boolean isEntityInWeb(Entity targetEntity) {
        Box entityBB = targetEntity.getBoundingBox();
        BlockPos blockPos = new BlockPos((int) targetEntity.getX(), (int) targetEntity.getY(), (int) targetEntity.getZ());
        BlockState blockState = targetEntity.getEntityWorld().getBlockState(blockPos);

        if (blockState.getBlock() == Blocks.COBWEB) {
            VoxelShape voxelShape = blockState.getOutlineShape(targetEntity.getEntityWorld(), blockPos);
            Box cobwebBB = voxelShape.getBoundingBox();
            Box intersection = entityBB.intersection(cobwebBB);
            return intersection.getXLength() > 0 && intersection.getYLength() > 0 && intersection.getZLength() > 0;
        }

        return false;
    }







    @Override
    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    @Override
    public int getTimeToNextWeb() {
        return this.dataTracker.get(TIME_TO_NEXT_WEB);
    }

    @Override
    public void setTimeToNextWeb(int timeToNextWeb) {
        this.dataTracker.set(TIME_TO_NEXT_WEB, timeToNextWeb);
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    private void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(SHOOTING, false);
    }










}
