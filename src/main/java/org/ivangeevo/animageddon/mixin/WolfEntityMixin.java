package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity implements Angerable
{
    @Unique
    private int dungDropTimer = 0;


    protected WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world)
    {
        super(entityType, world);
    }


    // Injected logic for dropping "Dung".
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo callbackInfo)
    {
        WolfEntity wolf = (WolfEntity) (Object) this;
        BlockPos wolfPos = wolf.getBlockPos();

        World world = wolf.getWorld();

        if (!world.isClient)
        {
            // Increment the dung drop timer
            dungDropTimer++;

            // Check if it's time to drop dung
            int minTicks = 39240;
            int maxTicks = 41350;
            if (dungDropTimer >= minTicks && dungDropTimer <= maxTicks && world.random.nextFloat() < 0.015) {

                // Drop dung item
                ItemStack dungItemStack = new ItemStack(ModItems.DUNG);
                wolf.dropStack(dungItemStack);

                world.playSound(null,wolfPos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS,0.2f,0.7f);

                // Reset the timer
                dungDropTimer = 0;
            }
        }
    }
}
