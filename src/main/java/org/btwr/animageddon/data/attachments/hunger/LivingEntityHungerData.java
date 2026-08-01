package org.btwr.animageddon.data.attachments.hunger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.btwr.animageddon.data.interfaces.HungerData;
import org.btwr.shared_library.api.data.EntityAttachmentBase;

public class LivingEntityHungerData implements EntityAttachmentBase<LivingEntity>, HungerData {

    public static Codec<LivingEntityHungerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("hunger_level").forGetter(LivingEntityHungerData::getHungerLevel),
                    Codec.INT.fieldOf("food_value_multiplier").forGetter(LivingEntityHungerData::getFoodValueMultiplier),
                    Codec.INT.fieldOf("graze_duration").forGetter(LivingEntityHungerData::getGrazeDuration)
            ).apply(instance, LivingEntityHungerData::new)
    );

    public static PacketCodec<ByteBuf, LivingEntityHungerData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    int hungerLevel;
    final int foodValueMultiplier;
    final int grazeDuration;

    int hungerCountdown;

    public LivingEntityHungerData(int foodValueMultiplier, int grazeDuration) {
        this.hungerLevel = 0;
        this.foodValueMultiplier = foodValueMultiplier;
        this.grazeDuration = grazeDuration;
    }

    public LivingEntityHungerData(int hungerLevel, int foodValueMultiplier, int grazeDuration) {
        this.hungerLevel = hungerLevel;
        this.foodValueMultiplier = foodValueMultiplier;
        this.grazeDuration = grazeDuration;
    }

    /** Default used by COW, SHEEP & HORSE **/
    public static LivingEntityHungerData forDefault() {
        return new LivingEntityHungerData(DEFAULT_FOOD_MULTIPLIER, DEFAULT_GRAZE_DURATION);
    }

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
    public void decrementHungerCountdown(int value) {
        this.hungerCountdown -= value;
    }
    public void resetHungerCountdown() {
        hungerCountdown = FULL_HUNGER_COUNT;
    }

    public int getGrazeDuration() {
        return grazeDuration;
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

    @Override
    public void tick(LivingEntity entity) {
        if (!(entity instanceof AnimalEntity animal)) return;
        if (!animal.isSubjectToHunger()) return;

        // Decrement the hunger countdown
        decrementHungerCountdown(animal.isBaby() ? 2 : 1);

        // Try setting a hunger level
        if (getHungerCountdown() <= 0) {
            if (!animal.isBaby()) {
                if (isFullyFed()) {
                    onBecomeFamished();
                } else if (isFamished()) {
                    onBecomeStarving();
                } else {
                    animal.onStarvingCountExpired();
                }

                resetHungerCountdown();
            } else {
                // Children cannot survive being famished. They'll
                // just keep taking damage once their countdown expires
                animal.damage(animal.getDamageSources().starve(), 1);
            }
        }
    }

}