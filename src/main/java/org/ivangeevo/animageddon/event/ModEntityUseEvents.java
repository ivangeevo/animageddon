package org.ivangeevo.animageddon.event;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.animageddon.data.MilkAttachedData;
import org.ivangeevo.animageddon.data.ModDataAttachments;

public class ModEntityUseEvents {

    public static void register() {
        onUseCowEntity();
        onUseMushroomEntity();
    }

    private static void onUseCowEntity() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            // Only handle cows that are alive and not babies
            if (entity instanceof CowEntity cow && cow.isAlive() && !cow.isBaby()) {
                // Ignore spectators
                if (player.isSpectator()) return ActionResult.PASS;

                ItemStack stack = player.getStackInHand(hand);

                // Only intercept bucket interactions
                if (stack.isIn(ConventionalItemTags.BUCKETS)) {

                    // Check if the cow can be milked
                    if (!cow.hasAttached(ModDataAttachments.MILK_DATA)) return ActionResult.FAIL;

                    MilkAttachedData data = cow.getAttached(ModDataAttachments.MILK_DATA);

                    if (!data.getCanBeMilked()) return ActionResult.FAIL;

                    // Custom milking logic
                    stack.decrement(1);
                    Item bucketItem = stack.getItem();

                    if (stack.isEmpty()) {
                        player.setStackInHand(hand, new ItemStack(bucketItem));
                    } else if (!player.getInventory().insertStack(new ItemStack(bucketItem))) {
                        player.dropItem(new ItemStack(bucketItem), false);
                    }

                    // Cow attacks player for milking
                    //cow.tryAttack(player);
                    cow.hurtTime = 10;

                    // Reset milk state and play sound
                    data.setMilked();
                    doMilkingEffects(cow);

                    // Interaction handled successfully — prevent vanilla milking
                    return ActionResult.SUCCESS;
                }
            }

            // Not a bucket, or not a cow — let other interactions happen
            return ActionResult.PASS;
        });
    }

    public static void doMilkingEffects(CowEntity cow) {
        World world = cow.getWorld();
        Vec3d pos = cow.getPos();

        // Play sound at the cow
        world.playSound(
                pos.x, pos.y, pos.z,
                SoundEvents.ENTITY_SLIME_ATTACK,
                SoundCategory.NEUTRAL,
                1.0F,
                (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 0.6F,
                false // not distant
        );

        // Spawn 50 snowball-like particles at the cow
        for (int i = 0; i < 50; i++) {
            double particleX = pos.x + world.random.nextDouble() - 0.5;
            double particleY = pos.y + 0.5; // adjust for cow body height
            double particleZ = pos.z + world.random.nextDouble() - 0.5;

            double velX = (world.random.nextDouble() - 0.5) * 0.5;
            double velY = world.random.nextDouble() * 0.25;
            double velZ = (world.random.nextDouble() - 0.5) * 0.5;

            world.addParticle(ParticleTypes.ITEM_SNOWBALL, particleX, particleY, particleZ, velX, velY, velZ);
        }
    }


    /** Modifications for Mushroom Cow entities
     * <p>1. Disable creating of Mushroom Stew
     * <p>2. Adds support for shearing them with all Conventional Tag Shear Tools from Fabric **/
    private static void onUseMushroomEntity() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (entity instanceof MooshroomEntity shroomCow) {
                if (shroomCow.isAlive() && stack.isOf(Items.BOWL)) {
                    return ActionResult.FAIL;
                }

                if (shroomCow.isShearable() && shroomCow.isAlive() &&
                        player.getStackInHand(hand).isIn(ConventionalItemTags.SHEAR_TOOLS) && !player.isSpectator()) {
                    shroomCow.sheared(SoundCategory.PLAYERS);
                    shroomCow.emitGameEvent(GameEvent.SHEAR, player);
                    if (!shroomCow.getWorld().isClient) {
                        stack.damage(1, player, EquipmentSlot.MAINHAND);
                    }
                    return ActionResult.success(shroomCow.getWorld().isClient);
                }
            }

            return ActionResult.PASS;
        });
    }
}
