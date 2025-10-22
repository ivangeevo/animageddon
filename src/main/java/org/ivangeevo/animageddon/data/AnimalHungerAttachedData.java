package org.ivangeevo.animageddon.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record AnimalHungerAttachedData(int hungerLevel) {

    public static final AnimalHungerAttachedData DEFAULT = new AnimalHungerAttachedData(0);

    public static Codec<AnimalHungerAttachedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
               Codec.INT.fieldOf("hunger_level").forGetter(AnimalHungerAttachedData::getHungerLevel)
            ).apply(instance, AnimalHungerAttachedData::new)
    );

    public static PacketCodec<ByteBuf, AnimalHungerAttachedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public int getHungerLevel() {
        return hungerLevel;
    }

    public boolean isFullyFed() {
        return this.getHungerLevel() == 0;
    }

    public boolean isFamished() {
        return this.getHungerLevel() == 1;
    }

    public boolean isStarving() {
        return this.getHungerLevel() == 2;
    }

}
