package org.ivangeevo.animageddon.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.entity.ai.goal.CustomWanderAroundGoal;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.ivangeevo.animageddon.entity.interfaces.EntityAdded;
import org.ivangeevo.animageddon.networking.packet.EntityEventPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends AnimalEntity implements CowEntityAdded
{

    @Shadow public abstract ActionResult interactMob(PlayerEntity player, Hand hand);

    protected CowEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }


    // Added variables

    private int kickAttackInProgressCounter = 0;
    private int kickAttackCooldownTimer = KICK_ATTACK_TICKS_TO_COOLDOWN;
    private int milkAccumulationCount = 0;



    public int kickAttackInProgressCounter() {return kickAttackInProgressCounter;}
    @Override
    public void setKickAttackInProgressCounter(int value) {kickAttackInProgressCounter = value;}
    private int kickAttackLegUsed = 0;
    public int kickAttackLegUsed() {return kickAttackLegUsed;}
    @Override
    public void setKickAttackLegUsed(int value) {kickAttackLegUsed = value;}

    @Inject(method = "initGoals", at = @At("HEAD"), cancellable = true)
    private void injectedInitGoals(CallbackInfo ci)
    {

        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.WHEAT, Items.GRASS), false));
        this.goalSelector.add(3, new TemptGoal(this, 1.4, Ingredient.ofItems(Items.CAKE), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));

        ci.cancel();

    }

    //@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void injectedInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getInventory().getMainHandStack();

        if ( stack != null && stack.getItem() == Items.BUCKET )
        {
            if ( gotMilk() )
            {
                stack.decrement(1);

                if ( stack.getCount() <= 0 )
                {
                    player.getInventory().setStack(player.getInventory().selectedSlot, new ItemStack( Items.BUCKET ) );
                }
                else if ( !player.getInventory().contains(Items.BUCKET.getDefaultStack()) )
                {
                    player.dropItem(Items.MILK_BUCKET);
                }

                tryAttack(this);

                if ( !getWorld().isClient )
                {
                    setGotMilk(false);

                    this.getWorld().playSound(null,this.getBlockPos(), SoundEvents.ENTITY_SLIME_ATTACK, SoundCategory.NEUTRAL, 1.0F, (getWorld().random.nextFloat() - getWorld().random.nextFloat()) * 0.2F + 0.6F);
                }
            }

            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        }

        cir.setReturnValue( interactMob(player, player.getActiveHand()));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() == Items.CAKE;
    }


    @Override
    public void tick()
    {
        super.tick();
        //updateKickAttack();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(GOT_MILK, false);
        this.dataTracker.startTracking(WEARING_BREEDING_HARNESS, false);

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("gotMilk", gotMilk());
        nbt.putInt("milkCount", milkAccumulationCount);

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (this.dataTracker.get(GOT_MILK))
        {
            setGotMilk(nbt.getBoolean("gotMilk"));
        }

        if ( nbt.contains( "milkCount" ) )
        {
            milkAccumulationCount = nbt.getInt("milkCount");
        }
    }


    @Override
    public boolean getWearingBreedingHarness() {
        return dataTracker.get(WEARING_BREEDING_HARNESS);
    }



    private void updateKickAttack()
    {
        if (kickAttackInProgressCounter >= 0 )
        {
            kickAttackInProgressCounter++;

            if (kickAttackInProgressCounter >= KICK_ATTACK_DURATION)
            {
                kickAttackInProgressCounter = -1;
            }
        }
        else if ( !this.getWorld().isClient ) // attacks are only launched on the server
        {
            kickAttackCooldownTimer--;


            // check if we should initiate an attack, which only applies if the cow is burning or has a target, which are the same conditions
            // that are used to determine if the cow is panicked and fleeing

            if (isAlive() && !isBaby() && !getWearingBreedingHarness() && kickAttackCooldownTimer <= 0 && (isOnFire() || getTarget() != null ) )
            {
                Vec3d kickCenter = computeKickAttackCenter();

                Box tipBox = new Box(
                        kickCenter.x - KICK_ATTACK_TIP_COLLISION_HALF_WIDTH,
                        kickCenter.y - KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT,
                        kickCenter.z - KICK_ATTACK_TIP_COLLISION_HALF_WIDTH,
                        kickCenter.x + KICK_ATTACK_TIP_COLLISION_HALF_WIDTH,
                        kickCenter.y + KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT,
                        kickCenter.z + KICK_ATTACK_TIP_COLLISION_HALF_WIDTH
                );

                List<Entity> potentialCollisionList = this.getWorld().getEntitiesByClass(Entity.class, tipBox, entity -> true);

                if ( !potentialCollisionList.isEmpty() )
                {
                    boolean bAttackLaunched = false;

                    Vec3d lineOfSightOrigin = new Vec3d( getX(), getY() + ( getHeight() / 2F ), getZ() );

                    for (Entity entity : potentialCollisionList) {
                        LivingEntity tempEntity = (LivingEntity) entity;

                        if (!(tempEntity instanceof CowEntity) && tempEntity.isAlive() && tempEntity.getVehicle() != this &&
                                canEntityBeSeenForAttackToCenterOfMass(tempEntity, lineOfSightOrigin)) {
                            bAttackLaunched = true;

                            kickAttackHitTarget(tempEntity);
                        }
                    }

                    if ( bAttackLaunched )
                    {
                        launchKickAttack();
                    }
                }
            }
        }
    }

    public Vec3d computeKickAttackCenter()
    {
        float fAttackAngle = MathHelper.wrapDegrees(getYaw() + 180F );

        double dPosX = (double)( -MathHelper.sin( fAttackAngle / 180.0F * (float)Math.PI ) ) * KICK_ATTACK_RANGE;
        double dPosY = this.getHeight() / 2F;
        double dPosZ = (double)( MathHelper.cos( fAttackAngle / 180.0F * (float)Math.PI ) ) * KICK_ATTACK_RANGE;

        dPosX += this.getX();
        dPosY += this.getY();
        dPosZ += this.getZ();

        return new Vec3d(dPosX, dPosY, dPosZ);
    }

    private void launchKickAttack()
    {
        kickAttackInProgressCounter = 0;
        kickAttackCooldownTimer = KICK_ATTACK_TICKS_TO_COOLDOWN;

        transmitKickAttackToClients();
    }

    private void transmitKickAttackToClients() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(getId());
            dataStream.writeByte((byte) EntityEventPacketHandler.COW_KICK_ATTACK_EVENT_ID);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Create a PacketByteBuf
        PacketByteBuf packetBuf = new PacketByteBuf(PacketByteBufs.create());
        // Use Fabric's networking API to send the packet to all players tracking the entity
        EntityEventPacketHandler.sendCustomPacketToClients((ServerWorld) this.getWorld(), this, packetBuf);
    }
    @Override
    public void onClientNotifiedOfKickAttack()
    {
        kickAttackInProgressCounter = 0;

        kickAttackLegUsed = random.nextInt(2);

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1F,
                ( random.nextFloat() - random.nextFloat() ) * 0.2F + 0.5F );
    }

    @Override
    public boolean gotMilk() {
        byte bGotMilk = dataTracker.get(GOT_MILK) ? (byte) 1 : (byte) 0;

        return bGotMilk != 0;

    }

    @Override
    public void setGotMilk(boolean bGotMilk) {
        dataTracker.set(GOT_MILK, bGotMilk);
    }



    //------------- Class Specific Methods ------------//




    public boolean canEntityBeSeenForAttackToCenterOfMass(Entity entity, Vec3d attackOrigin) {
        Vec3d centerOfMass = new Vec3d(entity.getX(), entity.getY() + (entity.getHeight() / 2.0), entity.getZ());

        RaycastContext context = new RaycastContext(
                attackOrigin, centerOfMass, RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE, entity
        );

        BlockHitResult result = entity.getWorld().raycast(context);

        return result.getType() == HitResult.Type.MISS;
    }


    private void kickAttackHitTarget(Entity hitEntity)
    {
        DamageSource cowSource = hitEntity.getDamageSources().mobAttack(this);

        int kickDamage = 7;

        if (hitEntity instanceof PlayerEntity) {
            kickDamage *= 1;
            //this.getWorld().getDifficulty().getCowKickStrengthMultiplier();
        }

        if ( hitEntity.damage( cowSource, kickDamage ) )
        {
            if ( isOnFire() && random.nextFloat() < 0.6F )
            {
                hitEntity.setOnFireFor( 4 );
            }

            ((EntityAdded)hitEntity).onKickedByCow((CowEntity)(Object)this);
        }
    }




}