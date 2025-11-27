package org.btwr.animageddon.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.data.attachments.hunger.LivingEntityHungerData;
import org.btwr.animageddon.tag.ModTags;

public class GrazeGoal extends Goal {

	private static final Predicate<BlockState> SHORT_GRASS_PREDICATE = BlockStatePredicate.forBlock(Blocks.SHORT_GRASS);

	private final AnimalEntity animal;
	private final World world;

	private int grazeProgressCounter;
	private int grazeCooldown = 0;

	public GrazeGoal(AnimalEntity animal) {
		this.animal = animal;
		this.world = animal.getWorld();
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
	}

	@Override
	public boolean canStart() {
		if (grazeCooldown > 0) {
			grazeCooldown--;
			return false;
		}

		if (isSubjectToHunger(animal)) {
			return animal.isHungryEnoughToGraze() && animal.getGrazeBlockForPos() != null;
		} else {
			return animal.getRandom().nextInt(animal.isBaby() ? 50 : 1000) == 0 &&
					animal.getGrazeBlockForPos() != null;
		}
	}

	private boolean isSubjectToHunger(AnimalEntity animal) {
		return animal.getType().isIn(ModTags.EntityTypes.SUBJECT_TO_HUNGER_ANIMALS);
	}

	@Override
	public void start() {
		this.grazeCooldown = 10;
		LivingEntityHungerData hungerData = animal.getAttached(ModDataAttachments.LIVING_ENTITY_HUNGER_DATA);
        assert hungerData != null;
        this.grazeProgressCounter = this.getTickCount(hungerData.getGrazeDuration());
		this.world.sendEntityStatus(this.animal, EntityStatuses.SET_SHEEP_EAT_GRASS_TIMER_OR_PRIME_TNT_MINECART);
		this.animal.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.grazeProgressCounter = 0;
	}

	@Override
	public boolean shouldContinue() {
		return this.grazeProgressCounter > 0;
	}

	@Override
	public void tick() {
		animal.setGrazeProgressCounter(Math.max(0, animal.getGrazeProgressCounter() - 1));

		if (animal.getGrazeProgressCounter() == 4) {
			BlockPos targetPos = animal.getGrazeBlockForPos();

			if (targetPos != null) {
				animal.onGrazeBlock(targetPos);

				//GroundCoverBlock.clearAnyGroundCoverRestingOnBlock(animal.worldObj, targetPos.x, targetPos.y, targetPos.z);

				if (animal.shouldNotifyBlockOnGraze()) {
					Block block = world.getBlockState(targetPos).getBlock();

					if (!block.getDefaultState().isAir()) {
						world.playSound(null, targetPos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS);
						block.btwr$onGrazed(world, targetPos, animal);
					}
				}
			}
		}

		/**
		AnimalHungerAttachedData hungerData = animal.getAttached(ModDataAttachments.ANIMAL_HUNGER_DATA);
		this.grazeProgressCounter = Math.max(0, this.grazeProgressCounter - 1);
		if (this.grazeProgressCounter == this.getTickCount(4)) {
			BlockPos blockPos = this.animal.getBlockPos();
			if (SHORT_GRASS_PREDICATE.test(this.world.getBlockState(blockPos))) {
                assert hungerData != null;
                animal.onGrazeBlock(blockPos);
				if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
					this.world.breakBlock(blockPos, false);
				}

				this.animal.onEatingGrass();
			} else {
				BlockPos blockPos2 = blockPos.down();
				if (this.world.getBlockState(blockPos2).isOf(Blocks.GRASS_BLOCK)) {
					if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
						this.world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos2, Block.getRawIdFromState(Blocks.GRASS_BLOCK.getDefaultState()));
						this.world.setBlockState(blockPos2, Blocks.DIRT.getDefaultState(), Block.NOTIFY_LISTENERS);
					}

					this.animal.onEatingGrass();
				}
			}
		}
		 **/
	}

}