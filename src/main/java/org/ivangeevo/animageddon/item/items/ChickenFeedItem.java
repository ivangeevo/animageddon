package org.ivangeevo.animageddon.item.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.ivangeevo.animageddon.util.ServerTimeHelper;

public class ChickenFeedItem extends Item {

    public ChickenFeedItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        // Allow feeding only of adult chickens
        if (!(entity instanceof ChickenEntity chicken) || chicken.isBaby()) {
            return ActionResult.PASS;
        }

        // Fail interaction if the chicken is already fed
        if (chicken.animageddon$getHasBeenFed()) {
            return ActionResult.FAIL;
        }




        return super.useOnEntity(stack, user, entity, hand);
    }

    /**
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!(entity instanceof ChickenEntity chicken) || chicken.isBaby()) {
            return ActionResult.PASS;
        }

        World world = user.getWorld();

        boolean fed = false;
        if (!world.isClient) {
            fed = feed(chicken);
            if (fed) {
                stack.decrement(1);
            }
        }

        // Play sound on both sides immediately for instant feedback
        if (fed || world.isClient) {
            world.playSound(
                    user,
                    chicken.getX(),
                    chicken.getY(),
                    chicken.getZ(),
                    SoundEvents.ENTITY_CHICKEN_HURT,
                    SoundCategory.NEUTRAL,
                    0.8F,
                    1.5F + chicken.getRandom().nextFloat() * 0.2F
            );
        }

        return ActionResult.SUCCESS;
    }
    **/



    private boolean feed(ChickenEntity chicken) {
        if (chicken.animageddon$getHasBeenFed()) return false;

        long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();

        // the following morning, at least half a day from now
        long timeToLayEgg = (((currentTime + 12000L) / 24000L) + 1) * 24000L;

        // crack of dawn (22550) + 30-second random variance
        timeToLayEgg += -1450 + chicken.getRandom().nextInt(600);

        chicken.animageddon$setTimeToLayEgg(timeToLayEgg);
        chicken.animageddon$setHasBeenFed(true);
        chicken.animageddon$setLastFedTime(currentTime);
        return true;
    }

}
