package org.btwr.animageddon.item.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.btwr.animageddon.item.ModItems;
import org.btwr.shared_library.api.item.ProgressiveCraftingItem;

public class BoneCarvingItem extends ProgressiveCraftingItem {

    public BoneCarvingItem(Settings settings) {
        super(settings);
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player) {
        player.playSound(
                SoundEvents.ENTITY_GENERIC_EAT,
                0.5F + 0.5F * (float)world.random.nextInt(2),
                (world.random.nextFloat() * 0.25F) + 1.25F
        );

        spawnUseParticles(stack, world, (PlayerEntity)player);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            player.playSound(
                    SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,
                    0.1F,
                    1.25F + (world.random.nextFloat() * 0.25F)
            );
        }

        return new ItemStack(ModItems.BONE_FISH_HOOK);
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        if (player.btwr$timesCraftedThisTick() == 0 && world.isClient) {
            player.playSound(
                    SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,
                    0.1F,
                    1.25F + (world.random.nextFloat() * 0.25F)
            );
        }

        super.onCraftByPlayer(stack, world, player);
    }

    protected void spawnUseParticles(ItemStack stack, World world, PlayerEntity player) {
        if (world.isClient) {
            Vec3d velVec = new Vec3d(
                    (world.random.nextFloat() - 0.5D) * 0.1D,
                    Math.random() * 0.1D + 0.1D,
                    0D
            );

            velVec = velVec.rotateX(-player.getPitch() * (float) Math.PI / 180F);
            velVec = velVec.rotateY(-player.getYaw() * (float) Math.PI / 180F);

            Vec3d posVec = new Vec3d(
                    (world.random.nextFloat() - 0.5D) * 0.3D,
                    (-world.random.nextFloat()) * 0.6D - 0.3D,
                    0.6D
            );

            posVec = posVec.rotateX(-player.getPitch() * (float) Math.PI / 180F);
            posVec = posVec.rotateY(-player.getYaw() * (float) Math.PI / 180F);

            posVec = posVec.add(
                    player.getX(),
                    player.getY() + player.getEyeHeight(player.getPose()),
                    player.getZ()
            );

            world.addParticle(
                    new ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                    posVec.x,
                    posVec.y,
                    posVec.z,
                    velVec.x,
                    velVec.y + 0.05D,
                    velVec.z
            );
        }
    }

}