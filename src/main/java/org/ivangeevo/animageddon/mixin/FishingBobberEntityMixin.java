package org.ivangeevo.animageddon.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity {


    @Shadow private int hookCountdown;

    @Shadow private int waitCountdown;

    @Shadow private int fishTravelCountdown;

    @Shadow @Final private static TrackedData<Boolean> CAUGHT_FISH;

    @Shadow private float fishAngle;

    @Shadow @Final private int lureLevel;

    @Shadow @Final private static TrackedData<Integer> HOOK_ENTITY_ID;

    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);

    public FishingBobberEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }



    // Overhaul the fishing logic to work similiarly to original BTW.
    @Inject(method = "tickFishingLogic", at = @At("HEAD"), cancellable = true)
        private void injectedTickFishingLogic(BlockPos pos, CallbackInfo ci)
    {

            ServerWorld serverWorld = (ServerWorld)this.getWorld();
            float i;
            long timeOfDay = serverWorld.getTimeOfDay() % 24000;


        // Check if it's a full moon and night time
        if ((serverWorld.getMoonPhase() == 0) && (timeOfDay >= 12700 && timeOfDay <= 22500))
        {
            i = 1.2f; // ? times chance during a full moon at night
        }
        else

        // Check if it's dawn or dusk (twilight)
        if (timeOfDay >= 21800 || timeOfDay >= 10800 && timeOfDay <= 13000)
        {
            i = 0.5f ; // ? times chance during dawn or dusk

        }
        else
        {
            i = -4f;
        }

        BlockPos blockPos = pos.up();

        if (this.random.nextFloat() < 0.5f && !this.getWorld().isSkyVisible(blockPos))
        {
            --i;
        }

        if (this.hookCountdown > 0)
            {
                --this.hookCountdown;

                if (this.hookCountdown <= 0)
                {
                    this.waitCountdown = 0;
                    this.fishTravelCountdown = 0;
                    this.getDataTracker().set(CAUGHT_FISH, false);
                }
            }
            else if (this.fishTravelCountdown > 0)
            {
                this.fishTravelCountdown -= i;

                if (this.fishTravelCountdown > 0)
                {
                    double j;
                    double e;
                    this.fishAngle += (float)this.random.nextTriangular(0.0, 9.188);
                    float f = this.fishAngle * ((float)Math.PI / 180);
                    float g = MathHelper.sin(f);
                    float h = MathHelper.cos(f);
                    double d = this.getX() + (double)(g * (float)this.fishTravelCountdown * 0.1f);
                    BlockState blockState = serverWorld.getBlockState(new BlockPos((int) d, (int) ((e = (float)MathHelper.floor(this.getY()) + 1.0f) - 1.0), (int) (j = this.getZ() + (double)(h * (float)this.fishTravelCountdown * 0.1f))));

                    if (blockState.isOf(Blocks.WATER))
                    {
                        if (this.random.nextFloat() < 0.15f)
                        {
                            serverWorld.spawnParticles(ParticleTypes.BUBBLE, d, e - (double)0.1f, j, 1, g, 0.1, h, 0.0);
                        }

                        float k = g * 0.04f;
                        float l = h * 0.04f;
                        serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, l, 0.01, -k, 1.0);
                        serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, -l, 0.01, k, 1.0);
                    }

                }
                else
                {
                    this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                    double m = this.getY() + 0.5;
                    serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
                    serverWorld.spawnParticles(ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
                    this.hookCountdown = MathHelper.nextInt(this.random, 20, 40);
                    this.getDataTracker().set(CAUGHT_FISH, true);
                }
            }
            else if (this.waitCountdown > 0)
            {
                this.waitCountdown -= i;
                float f = 0.15f;
                if (this.waitCountdown < 20)
                {
                    f += (float)(20 - this.waitCountdown) * 0.05f;
                }
                else if (this.waitCountdown < 40)
                {
                    f += (float)(40 - this.waitCountdown) * 0.02f;
                }
                else if (this.waitCountdown < 60)
                {
                    f += (float)(60 - this.waitCountdown) * 0.01f;
                }
                if (this.random.nextFloat() < f)
                {
                    double j;
                    double e;
                    float g = MathHelper.nextFloat(this.random, 0.0f, 360.0f) * ((float)Math.PI / 180);
                    float h = MathHelper.nextFloat(this.random, 25.0f, 60.0f);
                    double d = this.getX() + (double)(MathHelper.sin(g) * h) * 0.1;
                    BlockState blockState = serverWorld.getBlockState(new BlockPos((int) d, (int) ((e = (float)MathHelper.floor(this.getY()) + 1.0f) - 1.0), (int) (j = this.getZ() + (double)(MathHelper.cos(g) * h) * 0.1)));
                    if (blockState.isOf(Blocks.WATER))
                    {
                        serverWorld.spawnParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.1f, 0.0, 0.1f, 0.0);
                    }
                }
                if (this.waitCountdown <= 0)
                {
                    this.fishAngle = MathHelper.nextFloat(this.random, 0.0f, 360.0f);
                    this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
                }
            }
            else
            {
                this.waitCountdown = MathHelper.nextInt(this.random, 100, 600);
                this.waitCountdown -= this.lureLevel * 20 * 5;
            }
            ci.cancel();
    }


    @Override
    public void initDataTracker()
    {
        this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
        this.getDataTracker().startTracking(CAUGHT_FISH, false);
    }
}
