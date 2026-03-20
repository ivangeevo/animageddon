package org.btwr.animageddon.item.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.btwr.shared_library.api.item.ProgressiveCraftingItem;

public class WebUntanglingItem extends ProgressiveCraftingItem {

    public WebUntanglingItem(Settings settings) {
        super(settings);
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player) {
        player.playSound(
                SoundEvents.ENTITY_SLIME_ATTACK, 0.125F, (world.getRandom().nextFloat() * 0.1F + 0.9F) / 20
        );
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        user.playSound(
                SoundEvents.ENTITY_SLIME_SQUISH_SMALL, 0.5F, world.getRandom().nextFloat() * 0.01F + 0.09F
        );

        return new ItemStack(Items.STRING);
    }

    // add crafting sound
}
