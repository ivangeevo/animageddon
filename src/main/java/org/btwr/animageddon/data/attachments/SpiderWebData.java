package org.btwr.animageddon.data.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.entity.projectile.SpiderWebEntity;

public class SpiderWebData {

    boolean isShooting;
    int timeToNextWeb;

    //public static final int TIME_BETWEEN_WEBS = (20 * 60 * 20); // a full day
    public static final int TIME_BETWEEN_WEBS = 20;

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

            if (webData.hasWeb()) {
                Vec3d vec3d = spiderEntity.getRotationVec(1.0F);
                double f = targetEntity.getX() - (spiderEntity.getX() + vec3d.x * 4.0);
                double g = targetEntity.getBodyY(0.5) - (0.5 + spiderEntity.getBodyY(0.5));
                double h = targetEntity.getZ() - (spiderEntity.getZ() + vec3d.z * 4.0);
                SpiderWebEntity cobwebEntity = new SpiderWebEntity(spiderEntity.getWorld(), spiderEntity, f, g, h);
                spiderEntity.getWorld().spawnEntity(cobwebEntity);
                webData.setTimeToNextWeb(SpiderWebData.TIME_BETWEEN_WEBS); // Set cooldown
            }
        }
    }

}