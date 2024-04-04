package org.animageddon.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.animageddon.item.interfaces.ItemAdded;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemAdded
{


    private static final int BASE_HERBIVORE_ITEM_FOOD_VALUE = (200 * 32 );




    @Shadow public abstract ItemStack getDefaultStack();

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, BlockState state) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, BlockState state) {
        return ItemAdded.super.canHarvestBlock(stack, world, state);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, BlockState state) {
        return ItemAdded.super.getStrVsBlock(stack, world, state);
    }

    @Override
    public void updateUsingItem(ItemStack stack, World world, PlayerEntity player) {

    }
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
