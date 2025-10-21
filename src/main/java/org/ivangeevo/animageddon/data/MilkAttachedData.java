package org.ivangeevo.animageddon.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class MilkAttachedData {

    private int ticks;
    private boolean gotMilk;

    public static final MilkAttachedData DEFAULT = new MilkAttachedData(0, false);

    public MilkAttachedData(int ticks, boolean gotMilk) {
        this.ticks = ticks;
        this.gotMilk = gotMilk;
    }

    public static final Codec<MilkAttachedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("ticks").forGetter(MilkAttachedData::getTicks),
                    Codec.BOOL.fieldOf("gotMilk").forGetter(MilkAttachedData::getCanBeMilked)
            ).apply(instance, MilkAttachedData::new)
    );

    public static PacketCodec<ByteBuf, MilkAttachedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public int getTicks() {
        return ticks;
    }

    public boolean getCanBeMilked() {
        return gotMilk;
    }

    public void setMilked() {
        this.gotMilk = false;
        this.ticks = 0;
    }

    /** Call this every tick to accumulate milk */
    public void tick() {
        if (gotMilk) return; // already milkable
        ticks++;
        if (ticks >= 24000) { // 20 minutes; full minecraft day
            gotMilk = true;
            ticks = 0;
        }
    }
}
