package org.ivangeevo.animageddon.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class ChickenEggAttachedData {

    private long timeToLayEgg;
    private boolean hasBeenFed;

    public static final ChickenEggAttachedData DEFAULT = new ChickenEggAttachedData(0, false);

    public ChickenEggAttachedData(long timeToLayEgg, boolean hasBeenFed) {
        this.timeToLayEgg = timeToLayEgg;
        this.hasBeenFed = hasBeenFed;
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

    public void tick(ChickenEntity chicken) {
        chicken.eggLayTime = Integer.MAX_VALUE;

        if (!chicken.isBaby() /**&& isFullyFed()**/ && timeToLayEgg > 0 && validateTimeToLayEgg(chicken.getWorld())) {
            if (chicken.getWorld().getTimeOfDay() > timeToLayEgg) {
                chicken.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0f, chicken.getSoundPitch());
                chicken.playSound(SoundEvents.ENTITY_CHICKEN_HURT, 1.0f, chicken.getSoundPitch());
                chicken.dropItem(Items.EGG);
                this.timeToLayEgg = 0;
                this.hasBeenFed = false;
            }
        }

    }

    private boolean validateTimeToLayEgg(World world) {
        long currentTime = world.getTime();
        long deltaTime = timeToLayEgg - currentTime;

        if (deltaTime > 48000L) {
            // we're more than 2 days before the time, something is wrong (like a time change command), so don't lay
            timeToLayEgg = 0;

            return false;
        }

        return true;
    }
}
