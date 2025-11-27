package org.btwr.animageddon.mixin.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.data.interfaces.HungerData;
import org.btwr.animageddon.entity.interfaces.AnimalEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityAddedMixin extends PassiveEntity implements AnimalEntityAdded, HungerData {

    protected AnimalEntityAddedMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public int grazeProgressCounter = 0;

    @Override
    public int getGrazeProgressCounter() {
        return grazeProgressCounter;
    }

    @Override
    public void setGrazeProgressCounter(int value) {
        grazeProgressCounter = value;
    }

    @Override
    public void initHungerWithVariance() {
        // prevent initially spawned animals from all eating at the same time.
        AnimalEntity animal = (AnimalEntity)(Object)this;

        var data = animal.getAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA);
        if (!animal.hasAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA) || data == null) return;

        if (animal.isSubjectToHunger()) {
            data.setHungerCountdown(FULL_HUNGER_COUNT - animal.getRandom().nextInt(data.getGrazeHungerGain()));
        }
    }

    @Override
    public boolean canGrazeOnBlock(BlockPos pos) {
        World world = this.getWorld();
        Block block = world.getBlockState(pos).getBlock();

        if (block != null) {
            return block.btwr$canBeGrazedOn(world, pos, (AnimalEntity)(Object)this);
        }

        return false;
    }

    @Override
    public BlockPos getGrazeBlockForPos() {
        BlockPos pos = this.getBlockPos();
        BlockPos targetPos = new BlockPos(
                MathHelper.floor(pos.getX()),
                (int)this.getBoundingBox().minY,
                MathHelper.floor(pos.getZ())
        );

        if (this.canGrazeOnBlock(targetPos)) {
            return targetPos;
        }
        else {
            //targetPos.y--;
            BlockPos newTargetPos = targetPos.down();

            if (canGrazeOnBlock(newTargetPos) ) {
                return newTargetPos;
            }
        }

        return null;
    }

    @Override
    public boolean isHungryEnoughToGraze() {
        var hungerData = this.getAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA);
        if (hungerData == null) return false;
        return !hungerData.isFullyFed() || hungerData.getHungerCountdown() + hungerData.getGrazeHungerGain() <= FULL_HUNGER_COUNT;
    }

    @Override
    public boolean shouldNotifyBlockOnGraze() {
        return true;
    }

    @Override
    public void onStarvingCountExpired() {
        // max health 20 wolves, 15 cows, 10 pigs, 8 sheep, 4 chicken
        // Keep the if check for when we add difficulty checks
        //if (this.getWorld().getDifficulty().canAnimalsStarve()) {
        this.damage(this.getDamageSources().starve(), 5);
        //}
    }

    @Override
    public void addToHungerCount(int addedHunger) {
        var hungerData = this.getAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA);
        if (hungerData == null) return;
        int hungerCountdown = hungerData.getHungerCountdown();

        //hungerCountdown += iAddedHunger;
        hungerData.setHungerCountdown(hungerCountdown + addedHunger);

        // don't level up immediately when full to prevent flickering state

        if (hungerCountdown > LEVEL_UP_HUNGER_COUNT) {
            int hungerLevel = hungerData.getHungerLevel();

            if (hungerLevel > 0) {
                //hungerCountdown -= FULL_HUNGER_COUNT;
                hungerData.setHungerCountdown(hungerCountdown - FULL_HUNGER_COUNT);

                //setHungerLevel(hungerLevel - 1);
                hungerData.setHungerLevel(hungerLevel - 1);
            }
        }
    }

    @Override
    public void onGrazeBlock(BlockPos pos) {
        var hungerData = this.getAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA);
        if (hungerData == null) return;
        addToHungerCount(hungerData.getGrazeHungerGain());
    }

    @Override
    public boolean isSubjectToHunger() {
        return false;
    }

    /** Helper method to instantiate subclasses of AnimalEntity more easily **/
    @Unique
    @SuppressWarnings("unchecked")
    private <T extends AnimalEntity> void forAnimalSubclass(Class<T> type, Consumer<T> action) {
        if (type.isInstance(this)) {
            action.accept((T)(Object)this);
        }
    }

}