package org.ivangeevo.animageddon.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpiderWebEntity extends ProjectileEntity implements FlyingItemEntity {

    public SpiderWebEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super(EntityType.FIREBALL, world);
        this.setOwner(owner);
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    @Override
    protected void onCollision(HitResult hit) {
        if (getWorld().isClient) return;

        switch (hit.getType()) {
            case ENTITY -> {
                EntityHitResult ehr = (EntityHitResult) hit;
                Entity e = ehr.getEntity();
                e.damage(e.getDamageSources().thrown(this, getOwner()), 0);

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
        ItemStack stack = new ItemStack(Items.REDSTONE);
        getWorld().spawnEntity(new ItemEntity(getWorld(),
                pos.getX() + getWorld().random.nextFloat() * 0.7 + 0.15,
                pos.getY() + getWorld().random.nextFloat() * 0.7 + 0.15,
                pos.getZ() + getWorld().random.nextFloat() * 0.7 + 0.15,
                stack
        ));
    }

    private boolean canReplace(BlockPos pos) {
        var state = getWorld().getBlockState(pos);
        return state.isAir() /**|| state.getBlock() instanceof YourReplaceableWebBlock**/;
    }

    private void handleEntityImpact(LivingEntity target) {
        BlockPos posFeet = new BlockPos((int) target.getX(), (int) (target.getY() - 0.01), (int) target.getZ());
        BlockPos posBody = target.getBlockPos();

        if (tryPlace(posFeet)) return;
        if (tryPlace(posBody)) return;

        spawnTangledWebItem(posBody);
    }

    private boolean tryPlace(BlockPos pos) {
        if (canReplace(pos)) {
            getWorld().setBlockState(pos, Blocks.COBWEB.getDefaultState());
            return true;
        }
        return false;
    }

    private void handleBlockImpact(BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos().offset(hit.getSide());

        if (!tryPlace(pos)) {
            spawnTangledWebItem(pos);
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
    public ItemStack getStack() {return Items.COBWEB.getDefaultStack();}

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public void onKickedByCow(CowEntity cow) {

    }

}