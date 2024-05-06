package org.ivangeevo.animageddon.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.ivangeevo.animageddon.item.interfaces.ItemAdded;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemAdded
{
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
