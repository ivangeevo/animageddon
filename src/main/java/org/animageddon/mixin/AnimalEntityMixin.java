package org.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.animageddon.entity.interfaces.AgeableEntity;
import org.animageddon.entity.interfaces.AnimalEntityAdded;
import org.animageddon.item.interfaces.ItemAdded;

import static org.animageddon.entity.interfaces.CowEntityAdded.HUNGER_LEVEL;
import static org.animageddon.entity.interfaces.CowEntityAdded.WEARING_BREEDING_HARNESS;


@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements AnimalEntityAdded, AgeableEntity
{

    @Shadow public abstract boolean isBreedingItem(ItemStack stack);
    @Shadow public abstract void setLoveTicks(int loveTicks);
    @Shadow private int loveTicks;
    @Shadow public abstract int getLoveTicks();
    @Shadow public abstract boolean isInLove();




    private int hungerCountdown = FULL_HUNGER_COUNT;
    @Override
    public int hungerCountdown() {
        return hungerCountdown;
    }

    @Override
    public void setHungerCountdown(int value) {
        hungerCountdown = value;
    }

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    //@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void customInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack heldItem = player.getInventory().getMainHandStack();

        if ( heldItem != null )
        {
            if ( isEdibleItem(heldItem) )
            {
                if (!this.getWorld().isClient && attemptToBeHandFedItem(heldItem) )
                {
                    heldItem.decrement(1);

                    if ( heldItem.getCount() <= 0 )
                    {
                        player.getInventory().setStack( player.getInventory().selectedSlot,
                                null );
                    }
                }

                cir.setReturnValue(ActionResult.success(true));
            }
        }

        cir.setReturnValue(super.interact( player, player.getActiveHand()));
    }




    //------------- Class Specific Methods ------------//

    public boolean attemptToBeHandFedItem(ItemStack stack)
    {
        return attemptToEatItem(stack);
    }

    public boolean attemptToEatItem(ItemStack stack)
    {
        int iFoodValue = getItemFoodValue(stack);

        if (attemptToEatItemForBreeding(stack) || (iFoodValue > 0 &&
                isHungryEnoughToEatLooseFood() ) )
        {
            addToHungerCount(iFoodValue);

            this.getWorld().sendEntityStatus( this, (byte)10 ); // trigger eating anim on client

            this.getWorld().playSound(null, getX(), getY() + getHeight(), getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 1.0f, 1.0f);

            return true;
        }

        return false;
    }

    public boolean isEdibleItem(ItemStack stack)
    {
        return isBreedingItem( stack ) || getItemFoodValue(stack) > 0;
    }

    public boolean isHungryEnoughToEatLooseFood()
    {
        return !isFullyFed() || hungerCountdown <= FULL_HUNGER_COUNT;
    }

    public int getItemFoodValue(ItemStack stack)
    {
        return ((ItemAdded)stack.getItem()).getHerbivoreFoodValue(stack.getDamage()) *
                getFoodValueMultiplier();
    }

    @Override
    public int getFoodValueMultiplier() {
        return 2;
    }

    public boolean attemptToEatItemForBreeding(ItemStack stack)
    {
        if (isBreedingItem( stack ) && isReadyToEatBreedingItem() )
        {
            onEatBreedingItem();

            return true;
        }

        return false;
    }

    public boolean isReadyToEatBreedingItem()
    {
        return isFullyFed() && getGrowingAge() == 0 && !isInLove();
    }

    public boolean isFullyFed()
    {
        return getHungerLevel() == 0;
    }

    public int getHungerLevel()
    {
        return dataTracker.get(HUNGER_LEVEL);
    }

    public void setHungerLevel(int iHungerLevel)
    {
        dataTracker.set(HUNGER_LEVEL, (byte) iHungerLevel);
    }

    @Override
    public void onEatBreedingItem() {
        setLoveTicks( 600 );
        targetSelector.clear(null);

        for( int iTempCount = 0; iTempCount < 7; iTempCount++ )
        {
            this.getWorld().addParticle(ParticleTypes.HEART,
                    getPosX() + (double)( ( random.nextFloat() * getWidth() * 2F ) - getWidth() ),
                    getPosY() + 0.5D + (double)( random.nextFloat() * getHeight() ),
                    getPosZ() + (double)( ( random.nextFloat() * getWidth() * 2.0F ) - getWidth() ),
                    random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D,
                    random.nextGaussian() * 0.02D ); // last 3 are velocity
        }
    }

    public boolean getWearingBreedingHarness()
    {
        return (dataTracker.get(WEARING_BREEDING_HARNESS));
    }

    public void setWearingBreedingHarness( boolean bWearingHarness )
    {
        if ( bWearingHarness ) { dataTracker.set(WEARING_BREEDING_HARNESS, true); }
    }





    // ---------- Class Specific Methods ---------- //
    public void addToHungerCount(int iAddedHunger)
    {
        hungerCountdown += iAddedHunger;

        // don't level up immediately when full to prevent flickering state

        if (hungerCountdown > LEVEL_UP_HUNGER_COUNT)
        {
            int iHungerLevel = getHungerLevel();

            if ( iHungerLevel > 0 )
            {
                hungerCountdown -= FULL_HUNGER_COUNT;

                setHungerLevel(iHungerLevel - 1);
            }
        }
    }


    private static final int LEVEL_UP_HUNGER_COUNT = (FULL_HUNGER_COUNT +
            (FULL_HUNGER_COUNT / 16 ) );



}
