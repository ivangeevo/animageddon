package org.btwr.animageddon.entity.projectile;

import com.bwt.entities.HorizontalMechPowerSourceEntity;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.btwr.animageddon.entity.ModEntities;
import org.btwr.animageddon.item.ModItems;

public class SpiderWebEntity extends ProjectileEntity implements FlyingItemEntity {

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
        this.setPosition(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);

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
        if (hit instanceof EntityHitResult ehr) {
            Entity entityHit = ehr.getEntity();

            entityHit.damage(getWorld().getDamageSources().thrown(this, getOwner()), 0);

            if (!getWorld().isClient) {
                if (isMechanicalEntity(entityHit)) {
                    onMechanicalImpact(entityHit);
                } else {
                    BlockPos pos = entityHit.getBlockPos();

                    // attempt to place at feet of entity first
                    if (!attemptToPlaceWebInBlock(pos.down()) && !attemptToPlaceWebInBlock(pos)) {
                        spawnTangledWebItem(pos);
                    }
                }
            }
        } else {
            if (!getWorld().isClient) {
                BlockHitResult bhr = (BlockHitResult) hit;
                BlockPos targetPos = bhr.getBlockPos().offset(bhr.getSide());

                if (!attemptToPlaceWebInBlock(targetPos)) {
                    spawnTangledWebItem(targetPos);
                }
            }
        }

        discard();
    }

    private boolean attemptToPlaceWebInBlock(BlockPos pos) {
        if (!canWebReplaceBlock(pos)) return false;

        getWorld().setBlockState(pos, Blocks.COBWEB.getDefaultState());
        return true;
    }

    private boolean canWebReplaceBlock(BlockPos pos) {
        BlockState state = getWorld().getBlockState(pos);
        return state.isAir() || state.isReplaceable();
    }

    public boolean isMechanicalEntity(Entity entity) {
        if (!FabricLoader.getInstance().isModLoaded("bwt")) return false;
        return entity instanceof HorizontalMechPowerSourceEntity;
    }

    private void onMechanicalImpact(Entity entityHit) {
        Vec3d velocity = getVelocity();
        Vec3d placeVec = getPos().add(velocity);
        BlockPos originalPos = BlockPos.ofFloored(placeVec);

        if (isPointInsideBoundingBox(placeVec, entityHit.getBoundingBox()) || !canWebReplaceBlock(BlockPos.ofFloored(placeVec))) {
            // if the impact is within the bounding box of the mechanical entity, get 1 block in opposite direction
            placeVec = placeVec.add(
                    -Math.signum(Math.round(velocity.x)),
                    -Math.signum(Math.round(velocity.y)),
                    -Math.signum(Math.round(velocity.z))
            );

            if (isPointInsideBoundingBox(placeVec, entityHit.getBoundingBox()) || !canWebReplaceBlock(BlockPos.ofFloored(placeVec))) {
                placeVec = placeVec.add(0, -1, 0); // if still inside, move down one block once
                if (isPointInsideBoundingBox(placeVec, entityHit.getBoundingBox()) || !canWebReplaceBlock(BlockPos.ofFloored(placeVec))) {
                    spawnTangledWebItem(originalPos); // if still inside, just spawn the item and return
                    return;
                }
            }
        }

        BlockPos finalPos = BlockPos.ofFloored(placeVec);
        if (!attemptToPlaceWebInBlock(finalPos)) { // already tried going down one block with earlier check
            spawnTangledWebItem(finalPos);
        }
    }

    private boolean isPointInsideBoundingBox(Vec3d hitVec, Box entityBoundingBox) {
        BlockPos pos = BlockPos.ofFloored(hitVec);
        Box fullBlockBox = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        return fullBlockBox.intersects(entityBoundingBox);
    }

    private void spawnTangledWebItem(BlockPos pos) {
        float f1 = 0.7F;

        double d = (getWorld().random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        double d1 = (getWorld().random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        double d2 = (getWorld().random.nextFloat() * f1) + (1.0F - f1) * 0.5D;

        ItemEntity itemEntity = new ItemEntity(getWorld(),
                pos.getX() + d,
                pos.getY() + d1,
                pos.getZ() + d2,
                new ItemStack(ModItems.TANGLED_WEB)
        );
        itemEntity.setPickupDelay(10);

        getWorld().spawnEntity(itemEntity);
    }

    @Override
    public ItemStack getStack() {
        return Items.COBWEB.getDefaultStack();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public void onKickedByCow(CowEntity cow) {}

}