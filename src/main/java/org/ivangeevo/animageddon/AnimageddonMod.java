package org.ivangeevo.animageddon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.animageddon.block.ModBlocks;
import org.ivangeevo.animageddon.event.ModEntityLootTableEvents;
import org.ivangeevo.animageddon.item.ModItems;
import org.ivangeevo.animageddon.item.items.WoolItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimageddonMod implements ModInitializer
{

    public static final String MOD_ID = "animageddon";

    public static final Logger LOGGER = LoggerFactory.getLogger("animageddon");


    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocksAndAddToGroups();
        ModItems.registerModItemsAndAddToGroups();
        ModEntityLootTableEvents.initialize();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack itemStack = player.getMainHandStack();


            /* Manual spectator check is necessary because AttackBlockCallbacks
               fire before the spectator check */
            if (entity instanceof MooshroomEntity shroomCow) {
                if (shroomCow.isShearable() && shroomCow.isAlive() &&
                        player.getMainHandStack().isIn(ConventionalItemTags.SHEAR_TOOLS) && !player.isSpectator()) {
                    shroomCow.sheared(SoundCategory.PLAYERS);
                    shroomCow.emitGameEvent(GameEvent.SHEAR, player);
                    if (!shroomCow.getWorld().isClient) {
                        itemStack.damage(1, player, EquipmentSlot.MAINHAND);
                    }
                    return ActionResult.success(shroomCow.getWorld().isClient);
                }
            }

            return ActionResult.PASS;
        });

        ColorProviderRegistry.ITEM.register(
                (itemStack, tintIndex) -> itemStack != null ? WoolItem.getClosestColorIndex(tintIndex) : 0);


    }
}
