package org.ivangeevo.animageddon.item.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.ivangeevo.animageddon.data.attachments.ChickenEggAttachedData;
import org.ivangeevo.animageddon.data.ModDataAttachments;
import org.ivangeevo.animageddon.util.ServerTimeHelper;

public class ChickenFeedItem extends Item {

    public ChickenFeedItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        // allow feeding only of adult chickens
        if (!(entity instanceof ChickenEntity chicken) || chicken.isBaby()) {
            return ActionResult.PASS;
        }

        long currentTime = ServerTimeHelper.getOverworldTimeOfDayServerOnly();
        ChickenEggAttachedData data = chicken.getAttached(ModDataAttachments.CHICKEN_EGG_DATA);
        if (data != null) {
            // don't try to feed if already fed
            if (data.getHasBeenFed()) return ActionResult.PASS;

            // the following morning, at least half a day from now
            long timeToLayEgg = (((currentTime + 12000L) / 24000L) + 1) * 24000L;

            // crack of dawn (22550) + 30-second random variance
            timeToLayEgg += -1450 + chicken.getRandom().nextInt(600);

            chicken.playSound(SoundEvents.ENTITY_CHICKEN_HURT, 1.0F, chicken.getRandom().nextFloat() * 0.2F + 1.5F);

            data.setTimeToLayEgg(timeToLayEgg);
            data.setHasBeenFed(true);
            chicken.setAttached(ModDataAttachments.CHICKEN_EGG_DATA, data);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

}