package org.animageddon.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class EatMeatGoal extends Goal {
    private final PathAwareEntity zombie;
    private int eatingTime;

    public EatMeatGoal(PathAwareEntity zombie) {
        this.zombie = zombie;
        this.setControls(EnumSet.of(Control.LOOK, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return !zombie.getMainHandStack().isEmpty() && isMeatItem(zombie.getMainHandStack().getItem().getDefaultStack());
    }

    @Override
    public void start() {
        eatingTime = 40; // You can adjust the eating time based on your preference
        zombie.getNavigation().stop();
    }

    @Override
    public boolean shouldContinue() {
        return eatingTime > 0;
    }

    @Override
    public void tick() {
        if (eatingTime > 0) {
            zombie.getLookControl().lookAt(zombie.getTarget(), (float) (zombie.getHeadYaw() + 20), (float) zombie.getPitch());
            eatingTime--;

            if (eatingTime == 20) {
                // Perform the eating action
                ItemStack itemStack = zombie.getMainHandStack();
                zombie.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F + 0.5F * zombie.getRandom().nextInt(2), (zombie.getRandom().nextFloat() - zombie.getRandom().nextFloat()) * 0.2F + 1.0F);
                zombie.getWorld().playSound(null, zombie.getX(), zombie.getY(), zombie.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.NEUTRAL, 0.5F, 0.5F + 0.5F * zombie.getRandom().nextInt(2));

                // Remove the eaten item
                if (itemStack.getCount() > 1) {
                    itemStack.decrement(1);
                } else {
                    zombie.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
        }
    }

    private boolean isMeatItem(ItemStack stack) {
        return stack.isOf(Items.MUTTON) ||
                stack.isOf(Items.BEEF) ||
                stack.isOf(Items.PORKCHOP) ||
                stack.isOf(Items.COOKED_MUTTON) ||
                stack.isOf(Items.COOKED_BEEF) ||
                stack.isOf(Items.COOKED_PORKCHOP);
    }
}
