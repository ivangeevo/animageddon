package org.ivangeevo.animageddon.item.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ItemAdded
{

    int getHerbivoreFoodValue(int iItemDamage);
    Item setHerbivoreFoodValue(int iFoodValue);

    Item setAsBasicHerbivoreFood();


}
