package org.ivangeevo.animageddon.event;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.ivangeevo.animageddon.data.attachments.CowMilkAttachedData;
import org.ivangeevo.animageddon.data.ModDataAttachments;

public class ModEntityUseEvents {

    public static void register() {
        onUseAnimalEntity();
        onUseCowEntity();
        onUseMushroomEntity();
    }

    private static void onUseAnimalEntity() {

    }

    private static void onUseCowEntity() {
            UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            // Only handle cows that are alive and not babies
            if (entity instanceof CowEntity cow && cow.isAlive() && !cow.isBaby()) {
                // Ignore spectators
                if (player.isSpectator()) return ActionResult.PASS;

                ItemStack stack = player.getStackInHand(hand);
                boolean isBucket = stack.getItem() instanceof BucketItem || stack.isIn(ConventionalItemTags.BUCKETS);

                // Only intercept bucket interactions
                if (isBucket) {
                    if (!cow.hasAttached(ModDataAttachments.MILK_DATA)) return ActionResult.FAIL;

                    CowMilkAttachedData data = cow.getAttached(ModDataAttachments.MILK_DATA);
                    assert data != null;
                    if (!data.getCanBeMilked()) {
                        tryMilking(cow, data, stack, player, false);
                        return ActionResult.SUCCESS_NO_ITEM_USED;
                    } else {
                        tryMilking(cow, data, stack, player, true);
                        return ActionResult.SUCCESS;
                    }
                }
            }

            // Let other interactions happen
            return ActionResult.PASS;
        });
    }

    private static void tryMilking(CowEntity cow, CowMilkAttachedData data, ItemStack stack, PlayerEntity player, boolean canMilk) {
        World world = cow.getWorld();
        if (!canMilk) {
            cow.hurtTime = 10;
            DamageSource damageSource = new DamageSource(
                    world.getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .entryOf(DamageTypes.IN_FIRE)
            );
            cow.damage(damageSource, 0);
        } else {
            stack.decrement(1);
            ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET);
            Hand hand = player.getActiveHand();

            if (stack.isEmpty()) {
                player.setStackInHand(hand, milkBucket);
            } else if (!player.getInventory().insertStack(milkBucket)) {
                player.dropItem(milkBucket, false);
            }

            // Cow "attack" animation flicker for milking
            cow.hurtTime = 10;

            data.setMilked();
            doMilkingEffects(cow);
        }

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
