package org.btwr.animageddon.data.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class ChickenEggAttachedData {

    private long timeToLayEgg;
    private boolean hasBeenFed;

    public ChickenEggAttachedData(long timeToLayEgg, boolean hasBeenFed) {
        this.timeToLayEgg = timeToLayEgg;
        this.hasBeenFed = hasBeenFed;
    }

    public static ChickenEggAttachedData forDefault() {
        return new ChickenEggAttachedData(0, false);
    }

    public static final Codec<ChickenEggAttachedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.fieldOf("timeToLayEgg").forGetter(ChickenEggAttachedData::getTimeToLayEgg),
                    Codec.BOOL.fieldOf("hasBeenFed").forGetter(ChickenEggAttachedData::getHasBeenFed)
            ).apply(instance, ChickenEggAttachedData::new)
    );

    public static PacketCodec<ByteBuf, ChickenEggAttachedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public long getTimeToLayEgg() {
        return timeToLayEgg;
    }

    public void setTimeToLayEgg(long value) {
        timeToLayEgg = value;
    }

    public boolean getHasBeenFed() {
        return hasBeenFed;
    }

    public void setHasBeenFed(boolean value) {
        hasBeenFed = value;
    }

    public void resetData() {
        this.timeToLayEgg = 0;
        this.hasBeenFed = false;
    }

    public boolean validateTimeToLayEgg(long currentTime) {
        long deltaTime = timeToLayEgg - currentTime;

        if (deltaTime > 48000L) {
            // we're more than 2 days before the time, something is wrong (like a time change command), so don't lay
            timeToLayEgg = 0;

            return false;
        }

        return true;
    }

}