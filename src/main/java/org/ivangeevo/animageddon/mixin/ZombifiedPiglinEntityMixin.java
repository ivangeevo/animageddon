package org.ivangeevo.animageddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglinEntity.class)
public abstract class ZombifiedPiglinEntityMixin extends ZombieEntity
{

    public ZombifiedPiglinEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    // Make zombie piglins only have a 30% chance to spawn with golden swords.
    @Inject(method = "initEquipment", at = @At("HEAD"), cancellable = true)
    private void onInitEquipment(Random random, LocalDifficulty localDifficulty, CallbackInfo ci)
    {
        ItemStack goldSword = new ItemStack(Items.GOLDEN_SWORD);
        // TODO: Probably also a good idea to set a custom damage, so they aren't all full durability.
        //sword.setDamage(1);

        if (random.nextFloat() < 0.3f)
        {
            this.equipStack(EquipmentSlot.MAINHAND, goldSword);
        }
        ci.cancel();

    }

    @Unique
    private int getSwordDamageRandom(Random random)
    {
        return random.nextFloat() < 0.7f ? 4 : ToolMaterials.GOLD.getDurability();
    }


}
