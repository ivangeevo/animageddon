package org.ivangeevo.animageddon.data.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class CowMilkAttachedData {

    private int ticks;
    private boolean gotMilk;

    public static final CowMilkAttachedData DEFAULT = new CowMilkAttachedData(0, false);

    public CowMilkAttachedData(int ticks, boolean gotMilk) {
        this.ticks = ticks;
        this.gotMilk = gotMilk;
    }

    public static final Codec<CowMilkAttachedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("ticks").forGetter(CowMilkAttachedData::getTicks),
                    Codec.BOOL.fieldOf("gotMilk").forGetter(CowMilkAttachedData::getCanBeMilked)
            ).apply(instance, CowMilkAttachedData::new)
    );

    public static PacketCodec<ByteBuf, CowMilkAttachedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

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
