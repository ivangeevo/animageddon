package org.ivangeevo.animageddon.mixin.item;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.ivangeevo.animageddon.item.interfaces.ItemAdded;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemAdded {

    private static final int BASE_HERBIVORE_ITEM_FOOD_VALUE = (200 * 32 );
    private int herbivoreFoodValue = 0;

    @Override
    public int getHerbivoreFoodValue(int iItemDamage) {
        return herbivoreFoodValue = iItemDamage;
    }

    @Override
    public Item setHerbivoreFoodValue(int herbivoreFoodValue) {
        this.herbivoreFoodValue = herbivoreFoodValue;
        return (Item) (Object) this;
    }

    @Override
    public Item setAsBasicHerbivoreFood() {
        return setHerbivoreFoodValue(BASE_HERBIVORE_ITEM_FOOD_VALUE);
    }

}