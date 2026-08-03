package org.btwr.animageddon.mixin.entity;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.item.ModComponents;
import org.btwr.animageddon.item.interfaces.BaitableFishingRod;
import org.btwr.animageddon.util.WorldUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity implements BaitableFishingRod {

    @Shadow private int hookCountdown;
    @Shadow private int waitCountdown;
    @Shadow private int fishTravelCountdown;

    @Shadow @Final private static TrackedData<Boolean> CAUGHT_FISH;
    @Shadow @Final private static TrackedData<Integer> HOOK_ENTITY_ID;

    public FishingBobberEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V",
            at = @At("TAIL")
    )
    private void copyBaitFromRod(PlayerEntity player, World world, int luck, int waitReduction, CallbackInfo ci) {

        FishingBobberEntity self = (FishingBobberEntity)(Object)this;

        if (player.getMainHandStack().contains(ModComponents.HAS_BAIT_COMPONENT)) {
            boolean baited = player.getMainHandStack().getOrDefault(ModComponents.HAS_BAIT_COMPONENT, false);
            self.setAttached(ModDataAttachments.HAS_BAIT, baited);
        }
    }

    // Overhaul the fishing logic to work similarly to the original BTW.
    @Inject(method = "tickFishingLogic", at = @At("HEAD"), cancellable = true)
    private void onTickFishingLogic(BlockPos pos, CallbackInfo ci) {
        FishingBobberEntity self = (FishingBobberEntity)(Object)this;

        if (!(getOwner() instanceof PlayerEntity player)) {
            return;
        }

        if (!hasBait(self)) {
            ci.cancel();
            return;
        }

        ServerWorld world = (ServerWorld)getWorld();

        // Keep vanilla bite timer.
        if (hookCountdown > 0) {
            --hookCountdown;

            if (hookCountdown <= 0) {
                waitCountdown = 0;
                fishTravelCountdown = 0;
                getDataTracker().set(CAUGHT_FISH, false);
            }

            ci.cancel();
            return;
        }

        if (!self.isTouchingWater()) {
            ci.cancel();
            return;
        }

        // BTW idle bubbles.
        if (random.nextInt(10) == 1 && isBodyOfWaterLargeEnoughForFishing(world, self)) {
            world.spawnParticles(
                    ParticleTypes.BUBBLE,
                    getX() + random.nextFloat() * 2.0F - 1.0F,
                    MathHelper.floor(getY()) + 1,
                    getZ() + random.nextFloat() * 2.0F - 1.0F,
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }

        int biteOdds = calculateBTWBiteOdds(world, pos);

        //System.out.println("Fishing tick | odds=" + biteOdds + " time=" + (world.getTimeOfDay() % 24000) + " moon=" + world.getMoonPhase() + " sky=" + world.isSkyVisible(pos.up()) + " water=" + isBodyOfWaterLargeEnoughForFishing(world, self));

        if (random.nextInt(biteOdds) == 0) {
            BlockPos bobberPos = self.getBlockPos();

            if (world.isSkyVisible(bobberPos.up()) && isBodyOfWaterLargeEnoughForFishing(world, self)) {
                startVanillaBite(world, player);
            }
        }

        ci.cancel();
    }
    @Override
    public void initDataTracker(DataTracker.Builder builder) {
        builder.add(HOOK_ENTITY_ID, 0);
        builder.add(CAUGHT_FISH, false);
    }

    @Unique
    private int calculateBTWBiteOdds(ServerWorld world, BlockPos pos) {
        int odds = 1500;

        long time = world.getTimeOfDay() % 24000L;

        if (time > 14000 && time < 22000) {
            if (world.getMoonPhase() == 0) {
                odds /= 8;
            } else {
                odds *= 4;

                if (world.hasRain(pos))
                    odds /= 2;
            }
        } else {
            if (time < 2000 ||
                    time > 22000 ||
                    (time > 10000 && time < 14000)) {
                odds /= 2;
            }

            if (world.hasRain(pos))
                odds /= 2;
        }

        return Math.max(1, odds);
    }

    @Unique
    private void startVanillaBite(ServerWorld world, PlayerEntity player) {
        playSound(
                SoundEvents.ENTITY_FISHING_BOBBER_SPLASH,
                0.5F,
                1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F
        );

        double y = getY() + 0.5;

        world.spawnParticles(
                ParticleTypes.BUBBLE,
                getX(),
                y,
                getZ(),
                (int)(1.0F + getWidth() * 20.0F),
                getWidth(),
                0,
                getWidth(),
                0.2
        );

        world.spawnParticles(
                ParticleTypes.FISHING,
                getX(),
                y,
                getZ(),
                (int)(1.0F + getWidth() * 20.0F),
                getWidth(),
                0,
                getWidth(),
                0.2
        );

        // BTW's 10-39 tick reaction window.
        hookCountdown = random.nextInt(30) + 10;

        getDataTracker().set(CAUGHT_FISH, true);

        if (random.nextInt(5) == 0) {
            setHasBait(player.getWeaponStack(), false);
        }
    }

    @Unique
    private boolean isBodyOfWaterLargeEnoughForFishing(ServerWorld world, FishingBobberEntity bobber) {
        int x = MathHelper.floor(bobber.getX());
        int y = MathHelper.floor(bobber.getY());
        int z = MathHelper.floor(bobber.getZ());

        int maxRadius = 2;

        for (int i = -maxRadius; i <= maxRadius; i++) {
            for (int j = -maxRadius; j <= 0; j++) {
                for (int k = -maxRadius; k <= maxRadius; k++) {
                    BlockPos checkPos = new BlockPos(x + i, y + j, z + k);
                    if (!WorldUtils.isWaterSourceBlock(world, checkPos)
                            && Math.abs(i) + Math.abs(j) + Math.abs(k) <= maxRadius)
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }


}