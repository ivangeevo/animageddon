package org.animageddon.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.animageddon.item.ModItems;

@Mixin(EggItem.class)
public abstract class EggItemMixin extends Item {
    public EggItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EGG_THROW,
                SoundCategory.PLAYERS, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClient)
        {
            EggEntity eggEntity = new EggEntity(world, user);
            eggEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);


                if (world.random.nextFloat() < 0.25f) // 25% (1/4) chance
                {
                    // 1/4 chance to drop raw_egg after eggEntity lands
                    eggEntity.dropItem(ModItems.EGG_RAW);
                }
                else
                {
                    eggEntity.setItem(itemStack);

                    world.spawnEntity(eggEntity);

                }
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        cir.setReturnValue(TypedActionResult.success(itemStack, world.isClient()));
    }
}
