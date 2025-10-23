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
    private int hungerCountdown;
    private final int foodValueMultiplier;
    private int grazeDuration;

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
                    Codec.INT.fieldOf("graze_progress_counter").forGetter(AnimalHungerAttachedData::getGrazeDuration)
            ).apply(instance, AnimalHungerAttachedData::new)
    );

    public static PacketCodec<ByteBuf, AnimalHungerAttachedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public int getHungerLevel() {
        return hungerLevel;
    }

    public void setHungerLevel(int hungerLevel) {
        this.hungerLevel = hungerLevel;
    }

    public int getGrazeDuration() {
        return grazeDuration;
    }

    public void setGrazeDuration(int value) {
        this.grazeDuration = value;
    }

    public boolean shouldNotifyOnBlockGraze(AnimalEntity animal) {
        return true;
    }

    public void resetHungerCountdown() {
        hungerCountdown = FULL_HUNGER_COUNT;
    }

    public boolean isSubjectToHunger(AnimalEntity animal) {
        return SUBJECT_TO_HUNGER_TYPES.contains(animal.getType());
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

    /**
     * Returns null if no valid graze block exists at location
     */
    public BlockPos getGrazeBlockForPos(AnimalEntity animal) {
        BlockPos pos = animal.getBlockPos();
        World world = animal.getWorld();
        BlockPos targetPos = new BlockPos(
                MathHelper.floor(pos.getX()),
                (int)animal.getBoundingBox().minY,
                MathHelper.floor(pos.getZ())
        );

        if (canGrazeOnBlock(world, targetPos)) {
            return targetPos;
        } else {
            //targetPos.y--;
            BlockPos newTargetPos = targetPos.down();

            if (canGrazeOnBlock(world, newTargetPos) ) {
                return targetPos;
            }
        }

        return null;
    }

    public boolean canGrazeOnBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.GRASS_BLOCK);
    }

    public void onGrazed(World world, BlockPos pos, AnimalEntity animal) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState());

        BlockState stateBelow = world.getBlockState(pos.down());

        if (stateBelow != null) {
            // temporary behavior for testing
            world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState());
        }
    }

    @Override
    public boolean isHungryEnoughToGraze() {
        return !isFullyFed() || hungerCountdown + getGrazeHungerGain() <= FULL_HUNGER_COUNT;
    }

    @Override
    public void onStarvingCountExpired(AnimalEntity animal) {
        animal.damage(animal.getDamageSources().starve(), 5);
    }

    @Override
    public void tick(AnimalEntity animal) {
        if (!isSubjectToHunger(animal)) return;

        hungerCountdown -= animal.isBaby() ? 2 : 1;

        if (hungerCountdown <= 0) {
            if (!animal.isBaby()) {
                switch (hungerLevel) {
                    case 0 -> this.onBecomeFamished();
                    case 1 -> this.onBecomeStarving();
                    case 2 -> this.onStarvingCountExpired(animal);
                }
                this.resetHungerCountdown();
            } else {
                // children can't survive being famished. they'll
                // just keep taking damage once their countdown expires
                animal.damage(animal.getDamageSources().starve(), 1);
            }
        }
    }

    @Override
    public void onGrazeBlock(AnimalEntity animal, BlockPos pos) {
        this.addToHungerCount(animal, this.getGrazeHungerGain());
    }

    @Override
    public void addToHungerCount(AnimalEntity animal, int addedHunger) {
        hungerCountdown += addedHunger;

        // don't level up immediately when full to prevent flickering state

        if (hungerCountdown > LEVEL_UP_HUNGER_COUNT) {
            int hungerLevel = getHungerLevel();

            if (hungerLevel > 0) {
                hungerCountdown -= FULL_HUNGER_COUNT;

                setHungerLevel(hungerLevel - 1);
            }
        }
    }

    @Override
    public int getGrazeHungerGain() {
        return BASE_GRAZE_FOOD_VALUE * this.getFoodValueMultiplier();
    }

    @Override
    public int getFoodValueMultiplier() {
        return foodValueMultiplier;
    }

    public void initHungerWithVariance(AnimalEntity animal) {
        // prevent initially spawned animals from all eating at the same time.

        if (isSubjectToHunger(animal)) {
            hungerCountdown = FULL_HUNGER_COUNT - animal.getRandom().nextInt(getGrazeHungerGain());
        }
    }

}
