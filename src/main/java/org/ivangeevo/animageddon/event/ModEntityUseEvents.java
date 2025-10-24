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
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
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

    private static void onUseAnimalEntity() {}

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
                    CowMilkAttachedData data = cow.getAttached(ModDataAttachments.MILK_DATA);
                    if (data == null) return ActionResult.PASS;
                    tryMilking(cow, data, stack, player, data.getCanBeMilked());
                    return ActionResult.SUCCESS;
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
            if (!world.isClient) {
                DamageSource damageSource = new DamageSource(
                        world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(DamageTypes.IN_FIRE)
                );
                cow.damage(damageSource, 0);
            }
        } else {
            // Cow "attack" animation flicker for milking
            cow.hurtTime = 10;

            if (!world.isClient) {
                stack.decrementUnlessCreative(1, player);
                ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET);
                Hand hand = player.getActiveHand();

                if (stack.isEmpty()) {
                    player.setStackInHand(hand, milkBucket);
                } else if (!player.getInventory().insertStack(milkBucket)) {
                    player.dropItem(milkBucket, false);
                }

                data.setMilked();
            }

            // Play sound at the cow
            cow.playSound(
                    SoundEvents.ENTITY_SLIME_ATTACK,
                    1.0F,
                    (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 0.6F
            );

            // Spawn 50 snowball-like particles at the cow
            if (world instanceof ServerWorld serverWorld) {

                double particleX = cow.getPos().x + serverWorld.random.nextDouble() - 0.5;
                double particleY = cow.getPos().y + 0.5;
                double particleZ = cow.getPos().z + serverWorld.random.nextDouble() - 0.5;

                double velX = (serverWorld.random.nextDouble() - 0.5) * 0.5;
                double velY = serverWorld.random.nextDouble() * 0.25;
                double velZ = (serverWorld.random.nextDouble() - 0.5) * 0.5;

                serverWorld.spawnParticles(
                        ParticleTypes.ITEM_SNOWBALL,
                        particleX,
                        particleY,
                        particleZ,
                        50,
                        velX,
                        velY,
                        velZ,
                        0.1
                );
            }

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
