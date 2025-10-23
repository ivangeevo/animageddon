package org.ivangeevo.animageddon.data.attachments.hunger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.data.interfaces.AnimalHungerData;

public class AnimalHungerAttachedData implements AnimalHungerData, AnimalHungerConstants {

    private int hungerLevel;
    private final int foodValueMultiplier;
    private int grazeDuration;

    private int hungerCountdown;

    public AnimalHungerAttachedData(int foodValueMultiplier, int grazeDuration) {
        this.hungerLevel = 0;
        this.foodValueMultiplier = foodValueMultiplier;
        this.grazeDuration = grazeDuration;
    }

    public AnimalHungerAttachedData(int hungerLevel, int foodValueMultiplier, int grazeDuration) {
        this.hungerLevel = hungerLevel;
        this.foodValueMultiplier = foodValueMultiplier;
        this.grazeDuration = grazeDuration;
    }

    /** Default used by COW, SHEEP & HORSE **/
    public static AnimalHungerAttachedData forDefault() {
        return new AnimalHungerAttachedData(DEFAULT_FOOD_MULTIPLIER, DEFAULT_GRAZE_DURATION);
    }

    public static Codec<AnimalHungerAttachedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("hunger_level").forGetter(AnimalHungerAttachedData::getHungerLevel),
                    Codec.INT.fieldOf("food_value_multiplier").forGetter(AnimalHungerAttachedData::getFoodValueMultiplier),
                    Codec.INT.fieldOf("graze_duration").forGetter(AnimalHungerAttachedData::getGrazeDuration)
            ).apply(instance, AnimalHungerAttachedData::new)
    );

    public static PacketCodec<ByteBuf, AnimalHungerAttachedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public int getHungerLevel() {
        return hungerLevel;
    }

    public void setHungerLevel(int hungerLevel) {
        this.hungerLevel = hungerLevel;
    }

    public int getHungerCountdown() {
        return hungerCountdown;
    }

    public void setHungerCountdown(int value) {
        this.hungerCountdown = value;
    }

    public int getGrazeDuration() {
        return grazeDuration;
    }

    public void setGrazeDuration(int value) {
        this.grazeDuration = value;
    }

    public void resetHungerCountdown() {
        hungerCountdown = FULL_HUNGER_COUNT;
    }

    public boolean isFullyFed() {
        return getHungerLevel() == 0;
    }

    public boolean isFamished() {
        return getHungerLevel() == 1;
    }

    public boolean isStarving()
    {
        return getHungerLevel() >= 2;
    }

    public void onBecomeFamished() {
        this.hungerLevel = 1;
    }

    public void onBecomeStarving() {
        this.hungerLevel = 2;
    }

    @Override
    public int getGrazeHungerGain() {
        return BASE_GRAZE_FOOD_VALUE * getFoodValueMultiplier();
    }

    @Override
    public int getFoodValueMultiplier() {
        return foodValueMultiplier;
    }

}
