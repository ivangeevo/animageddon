package org.btwr.animageddon.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.btwr.animageddon.entity.ModEntities;
import org.btwr.animageddon.item.ModItems;

public class SpiderWebEntity extends ProjectileEntity implements FlyingItemEntity {

    public SpiderWebEntity(World world, LivingEntity owner, Entity target) {
        super(ModEntities.SPIDER_WEB, world);
        this.setOwner(owner);
        this.refreshPositionAndAngles(owner.getX(), owner.getEyeY(), owner.getZ(), owner.getYaw(), owner.getPitch());

        Vec3d vec3d = owner.getPos();

        vec3d = vec3d.subtract(
                MathHelper.cos(this.getYaw() / 180.0F * (float)Math.PI) * 0.16F,
                0.2D,
                MathHelper.sin(this.getYaw() / 180.0F * (float)Math.PI) * 0.16F
        );

        this.setPosition(vec3d);

        double deltaX = target.getX() - this.getX();
        double deltaY = target.getY() - this.getY();
        double deltaZ = target.getZ() - this.getZ();

        //this.setVelocity(deltaX, deltaY, deltaZ);
        this.setVelocity(deltaX, deltaY, deltaZ, 0.8f, 1.0f);
    }

    public SpiderWebEntity(EntityType<SpiderWebEntity> type, World world) {
        super(type, world);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.SPIDER_WEB;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3d velocity = this.getVelocity();

        // move entity
        this.setPosition(
                this.getX() + velocity.x,
                this.getY() + velocity.y,
                this.getZ() + velocity.z
        );

        // apply velocity
        this.setVelocity(velocity);

        // collision check
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }
    }

    @Override
    protected void onCollision(HitResult hit) {
        if (getWorld().isClient) return;

        switch (hit.getType()) {
            case ENTITY -> {
                EntityHitResult ehr = (EntityHitResult) hit;
                Entity e = ehr.getEntity();

                if (e instanceof LivingEntity living) {
                    handleEntityImpact(living);
                } else {
                    spawnTangledWebItem(e.getBlockPos());
                }
            }

            case BLOCK -> handleBlockImpact((BlockHitResult) hit);
        }

        discard();
    }

    private void spawnTangledWebItem(BlockPos pos) {
        ItemStack stack = new ItemStack(ModItems.TANGLED_WEB);
        getWorld().spawnEntity(new ItemEntity(getWorld(),
                pos.getX() + getWorld().random.nextFloat() * 0.7 + 0.15,
                pos.getY() + getWorld().random.nextFloat() * 0.7 + 0.15,
                pos.getZ() + getWorld().random.nextFloat() * 0.7 + 0.15,
                stack
        ));
    }

    private boolean canWebReplaceBlock(BlockPos pos) {
        BlockState state = getWorld().getBlockState(pos);
        return state.isAir();
    }

    private void handleEntityImpact(LivingEntity target) {
        BlockPos posBody = target.getBlockPos();

        if (!attemptToPlaceWebInBlock(posBody.down()) && !attemptToPlaceWebInBlock(posBody)) {
            spawnTangledWebItem(posBody);
        }
    }


    private boolean attemptToPlaceWebInBlock(BlockPos pos) {
        if (canWebReplaceBlock(pos)) {
            this.getWorld().setBlockState(pos, Blocks.COBWEB.getDefaultState());
            return true;
        }
        return false;
    }

    private void handleBlockImpact(BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos().offset(hit.getSide());

        if (!attemptToPlaceWebInBlock(pos)) {
            this.spawnTangledWebItem(pos);
        }
    }

    private void placeCobweb(BlockPos blockPos) {
        if (this.getWorld().getBlockState(blockPos.down()).isFullCube(this.getWorld(), blockPos.down())) {
            this.getWorld().setBlockState(blockPos, Blocks.COBWEB.getDefaultState());
        }
    }

    private void placeCobwebAtEntity(BlockPos blockPos, LivingEntity targetEntity) {
        if (this.getWorld().getBlockState(blockPos.down()).isFullCube(this.getWorld(), blockPos.down())) {
            this.getWorld().setBlockState(BlockPos.ofFloored(targetEntity.getBlockPos().toBottomCenterPos()), Blocks.COBWEB.getDefaultState());
        }
    }

    @Override
    public ItemStack getStack() {
        return Items.COBWEB.getDefaultStack();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public void onKickedByCow(CowEntity cow) {

    }

}