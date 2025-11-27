package org.ivangeevo.animageddon.item.interfaces;

import net.minecraft.item.Item;

public interface ItemAdded
{

    int getHerbivoreFoodValue(int iItemDamage);

    Item setHerbivoreFoodValue(int iFoodValue);

    Item setAsBasicHerbivoreFood();

}