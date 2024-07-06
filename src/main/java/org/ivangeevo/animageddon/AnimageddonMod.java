package org.ivangeevo.animageddon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.animageddon.item.AnimaggedonModItemGroup;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.tag.BTWRConventionalTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimageddonMod implements ModInitializer
{

    public static final String MOD_ID = "animageddon";

    public static final String MOD_VERSION = "1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("animageddon");


    @Override
    public void onInitialize()
    {
        AnimaggedonModItemGroup.registerItemGroups();
        //ModBlock.registerModBlocks();
        ModItems.registerModItems();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack itemStack = player.getMainHandStack();


            /* Manual spectator check is necessary because AttackBlockCallbacks
               fire before the spectator check */
            if (entity instanceof MooshroomEntity shroomCow)
            {
                if (shroomCow.isShearable() && shroomCow.isAlive() &&
                        player.getMainHandStack().isIn(BTWRConventionalTags.Items.SHEARS) && !player.isSpectator()) {
                    shroomCow.sheared(SoundCategory.PLAYERS);
                    shroomCow.emitGameEvent(GameEvent.SHEAR, player);
                    if (!shroomCow.getWorld().isClient) {
                        itemStack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
                    }
                    return ActionResult.success(shroomCow.getWorld().isClient);
                }
            }

            return ActionResult.PASS;
        });

    }
}
