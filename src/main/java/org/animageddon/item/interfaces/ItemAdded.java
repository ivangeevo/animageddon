package org.animageddon.item.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ItemAdded
{



    default boolean canHarvestBlock(ItemStack stack, World world, BlockState state) {
        return false;
    }
    default float getStrVsBlock(ItemStack stack, World world, BlockState state) {
        return 1F;
    }
    boolean isEfficientVsBlock(ItemStack stack, World world, BlockState state);

    int getHerbivoreFoodValue(int iItemDamage);
    Item setHerbivoreFoodValue(int iFoodValue);

    Item setAsBasicHerbivoreFood();


    void updateUsingItem(ItemStack stack, World world, PlayerEntity player);

}
