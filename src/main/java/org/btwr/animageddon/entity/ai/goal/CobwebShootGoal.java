package org.btwr.animageddon.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.animageddon.data.ModDataAttachments;

import java.util.EnumSet;

public class CobwebShootGoal extends Goal {

    final SpiderEntity spider;
    int cooldown;
    private int targetNotVisibleTicks;

    public CobwebShootGoal(SpiderEntity spider) {
        this.spider = spider;
        this.setControls(EnumSet.noneOf(Control.class));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.spider.getTarget();
        return livingEntity != null && livingEntity.isAlive() && this.spider.canTarget(livingEntity);
    }

    @Override
    public void start() {
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        var webData = spider.getAttached(ModDataAttachments.SPIDER_WEB_DATA);
        if (webData != null) {
            webData.setShooting(false);
        }
        this.targetNotVisibleTicks = 0;

    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        // New
        this.cooldown--;
        LivingEntity livingEntity = this.spider.getTarget();

        var webData = spider.getAttached(ModDataAttachments.SPIDER_WEB_DATA);
        if (webData == null) return;

        if (livingEntity != null) {
            boolean bl = this.spider.getVisibilityCache().canSee(livingEntity);
            if (bl) {
                this.targetNotVisibleTicks = 0;
            } else {
                this.targetNotVisibleTicks++;
            }

            double d = this.spider.squaredDistanceTo(livingEntity);
            if (d < 100 && bl) {
                if (webData.hasWeb()) {
                    webData.tick(spider, livingEntity);
                    this.spider.getLookControl().lookAt(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }

            }
            else if (this.targetNotVisibleTicks < 5) {
                this.spider.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0);
            }

            super.tick();
        }
    }

    public static boolean isTargetInBlock(LivingEntity entity, Block targetBlock) {
        Box box = entity.getBoundingBox();

        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.floor(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.floor(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.floor(box.maxZ);

        BlockPos.Mutable pos = new BlockPos.Mutable();
        World world = entity.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {

                    pos.set(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (state.isOf(targetBlock)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}