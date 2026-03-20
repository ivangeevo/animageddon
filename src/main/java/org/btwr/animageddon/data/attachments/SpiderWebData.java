package org.btwr.animageddon.data.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.btwr.animageddon.block.ModBlocks;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.entity.ModEntities;
import org.btwr.animageddon.entity.projectile.SpiderWebEntity;

public class SpiderWebData {

    boolean isShooting;
    int timeToNextWeb;

    public static final int TIME_BETWEEN_WEBS = (20 * 60 * 20); // a full day
    //public static final int TIME_BETWEEN_WEBS = 60;

    public SpiderWebData(boolean isShooting, int timeToNextWeb) {
        this.isShooting = isShooting;
        this.timeToNextWeb = timeToNextWeb;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean value) {
        isShooting = value;
    }

    public int timeToNextWeb() {
        return timeToNextWeb;
    }

    public void setTimeToNextWeb(int value) {
        timeToNextWeb = value;
    }

    public boolean hasWeb() {
        return timeToNextWeb <= 0;
    }

    public void decrementTimeToNextWeb() {
        timeToNextWeb--;
    }

    public static Codec<SpiderWebData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("is_shooting").forGetter(SpiderWebData::isShooting),
                    Codec.INT.fieldOf("time_to_next_web").forGetter(SpiderWebData::timeToNextWeb)
            ).apply(instance, SpiderWebData::new)
    );

    public static PacketCodec<ByteBuf, SpiderWebData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public void tick(SpiderEntity spiderEntity, Entity targetEntity) {
        if (!spiderEntity.getWorld().isClient()) {
            var webData = spiderEntity.getAttached(ModDataAttachments.SPIDER_WEB_DATA);
            assert webData != null;

            World world = spiderEntity.getWorld();

            boolean isInWeb = isTargetInBlock(targetEntity, Blocks.COBWEB) || isTargetInBlock(targetEntity, ModBlocks.WEB_BLOCK);
            boolean canShootAtTarget = spiderEntity.getWorld().getRandom().nextInt(10) == 0 && !(targetEntity.getType() == EntityType.SPIDER);

            if (!isInWeb && canShootAtTarget) {
                SpiderWebEntity webEntity = new SpiderWebEntity(ModEntities.SPIDER_WEB, world);
                Vec3d look = spiderEntity.getRotationVec(1.0F);

                webEntity.setPosition(
                        spiderEntity.getX() + look.x,
                        spiderEntity.getEyeY() - 0.1,
                        spiderEntity.getZ() + look.z
                );

                Vec3d dir = targetEntity.getPos().subtract(spiderEntity.getEyePos()).normalize();

                webEntity.setVelocity(dir.x, dir.y, dir.z, 1.5f, 0f);

                world.spawnEntity(webEntity);

                webData.setShooting(true);
                webData.setTimeToNextWeb(SpiderWebData.TIME_BETWEEN_WEBS); // Set cooldown
            }
        }
    }

    public static boolean isTargetInBlock(Entity entity, Block targetBlock) {
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