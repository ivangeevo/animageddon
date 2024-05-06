package org.ivangeevo.animageddon.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CobwebEntity extends ProjectileEntity implements FlyingItemEntity
{

    public CobwebEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ)
    {
        super(EntityType.FIREBALL, world);
        this.setOwner(owner);
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    @Override
    protected void onCollision(HitResult hitResult)
    {
        if (!this.getWorld().isClient)
        {
            if (hitResult.getType() == HitResult.Type.BLOCK)
            {
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos().offset(((BlockHitResult) hitResult).getSide());
                this.placeCobweb(blockPos);
            } else if (hitResult.getType() == HitResult.Type.ENTITY)
            {
                Entity entity = ((EntityHitResult) hitResult).getEntity();
                entity.damage(entity.getDamageSources().thrown(this, this.getOwner()), 0.0F);
                placeCobwebAtEntity(getBlockPos(), (LivingEntity) entity);
            }
            this.remove(RemovalReason.DISCARDED);
        }
    }

    private void placeCobweb(BlockPos blockPos)
    {
        if (this.getWorld().getBlockState(blockPos.down()).isFullCube(this.getWorld(), blockPos.down())) {
            this.getWorld().setBlockState(blockPos, Blocks.COBWEB.getDefaultState());
        }
    }

    private void placeCobwebAtEntity(BlockPos blockPos, LivingEntity targetEntity)
    {
        if (this.getWorld().getBlockState(blockPos.down()).isFullCube(this.getWorld(), blockPos.down())) {
            this.getWorld().setBlockState(targetEntity.getBlockPos(), Blocks.COBWEB.getDefaultState());
        }
    }

    @Override
    public ItemStack getStack() {return Items.COBWEB.getDefaultStack();}

    @Override
    protected void initDataTracker() {}


    @Override
    public void onKickedByCow(CowEntity cow) {

    }
}
