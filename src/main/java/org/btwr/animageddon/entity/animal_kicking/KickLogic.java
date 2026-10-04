package org.btwr.animageddon.entity.animal_kicking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.btwr.animageddon.data.ModDataAttachments;
import org.btwr.animageddon.tag.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class KickLogic {
    public static final byte STATUS_KICK_LEFT = 100;
    public static final byte STATUS_KICK_RIGHT = 101;

    public static final int COOLDOWN_TICKS = 40;
    private static final double RANGE = 1.75;
    private static final double BOX_WIDTH = 2.75;
    private static final double BOX_HEIGHT = 2.0;
    private static final double CLOSE_RANGE_SQ = 1.25 * 1.25;
    private static final int DANGER_WINDOW_TICKS = 100; // BTW revenge-target duration

    /** Turn the back to the threat on the tick the kick is ready so it lands instantly. */
    private static final boolean FACE_AWAY_BEFORE_KICK = true;
    private static final double FACE_AWAY_RANGE_SQ = 3.5 * 3.5;

    private static final double KICK_FLING_MULTIPLIER = 1.0; // BTW getAnimalKickMovementMultiplier() default

    public static void serverTick(AnimalEntity self, ServerWorld world) {
        if (!self.isAlive() || self.isBaby()) return;

        LivingEntity threat = getThreat(self);
        if (threat == null && !self.isOnFire()) return;

        if (!self.getType().isIn(ModTags.EntityTypes.KICKING_ANIMALS)) return;
        if (world.getDifficulty() == Difficulty.PEACEFUL) return;

        long now = world.getTime();
        if (now < self.getAttachedOrElse(ModDataAttachments.ANIMAL_KICK_DATA, 0L)) return;

        if (FACE_AWAY_BEFORE_KICK && threat != null && !self.hasPassengers()
                && self.squaredDistanceTo(threat) <= FACE_AWAY_RANGE_SQ) {
            faceAwayFrom(self, threat);
        }

        Vec3d center = kickCenter(self);
        Box box = Box.of(center, BOX_WIDTH, BOX_HEIGHT, BOX_WIDTH);
        Vec3d eye = new Vec3d(self.getX(), self.getY() + self.getHeight() / 2.0, self.getZ());

        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, box, e ->
                e != self
                        && e.isAlive()
                        && !e.isSpectator()
                        && !self.hasPassenger(e)
                        && !e.getType().isIn(ModTags.EntityTypes.KICKING_ANIMALS)
                        && !isOwner(self, e)
                        && !(e instanceof PlayerEntity p && p.isCreative())
                        && (canSee(world, self, eye, e) || horizontalDistSq(self, e) <= CLOSE_RANGE_SQ));

        if (targets.isEmpty()) return;

        for (LivingEntity target : targets) {
            hit(self, target, world);
        }

        self.setAttached(ModDataAttachments.ANIMAL_KICK_DATA, now + COOLDOWN_TICKS);

        float pitch = (self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2f + 0.5f;
        world.playSound(null, self.getX(), self.getY(), self.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0f, pitch);

        int leg = self.getRandom().nextInt(2); // server picks the leg so every client agrees
        world.sendEntityStatus(self, leg == 0 ? STATUS_KICK_LEFT : STATUS_KICK_RIGHT);
    }

    @Nullable
    public static LivingEntity getThreat(AnimalEntity e) {
        KickAccess access = (KickAccess) e;
        LivingEntity attacker = access.animageddon$getLastAttacker();
        long hurt = access.animageddon$getLastHurtTime();
        if (attacker == null || hurt < 0 || !attacker.isAlive() || attacker.getWorld() != e.getWorld()) {
            return null;
        }
        return e.getWorld().getTime() - hurt < DANGER_WINDOW_TICKS ? attacker : null;
    }

    private static void faceAwayFrom(AnimalEntity self, LivingEntity threat) {
        double dx = threat.getX() - self.getX();
        double dz = threat.getZ() - self.getZ();
        if (dx * dx + dz * dz < 1.0E-4) return;

        float yaw = (float) (Math.atan2(dx, -dz) * 180.0 / Math.PI);
        self.setYaw(yaw);
        self.setBodyYaw(yaw);
        self.setHeadYaw(yaw);
        self.prevYaw = yaw;
        self.prevBodyYaw = yaw;
        self.prevHeadYaw = yaw;
    }

    private static boolean isOwner(AnimalEntity self, LivingEntity other) {
        return self instanceof AbstractHorseEntity horse && other.getUuid().equals(horse.getOwnerUuid());
    }

    private static double horizontalDistSq(Entity a, Entity b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return dx * dx + dz * dz;
    }

    private static Vec3d kickCenter(AnimalEntity e) {
        float yaw = e.getYaw() * MathHelper.RADIANS_PER_DEGREE;
        return new Vec3d(
                e.getX() + MathHelper.sin(yaw) * RANGE,
                e.getY() + e.getHeight() / 2.0,
                e.getZ() - MathHelper.cos(yaw) * RANGE);
    }

    private static boolean canSee(World world, Entity from, Vec3d eye, Entity to) {
        Vec3d end = new Vec3d(to.getX(), to.getY() + to.getHeight() / 2.0, to.getZ());
        return world.raycast(new RaycastContext(eye, end,
                        RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, from))
                .getType() == HitResult.Type.MISS;
    }

    private static float damageFor(AnimalEntity self) {
        return self instanceof AbstractDonkeyEntity ? 5.0f : 7.0f;
    }

    private static void hit(AnimalEntity self, LivingEntity target, ServerWorld world) {
        float damage = damageFor(self);
        if (target instanceof PlayerEntity) {
            // TODO: Damage should be different based on the BTWR difficulty system when it's implemented.
            //  The below is just a placeholder
            /**
            damage *= switch (world.getDifficulty()) {
                case EASY -> 0.5f;
                case HARD -> 1.5f;
                default -> 1.0f;
            };
             **/

        }
        if (target.damage(self.getDamageSources().mobAttack(self), damage)) {
            if (self.isOnFire() && self.getRandom().nextFloat() < 0.6f) {
                target.setOnFireFor(4);
            }
            flingAwayFromEntity(target, self, KICK_FLING_MULTIPLIER); // after damage, same order as BTW
        }
    }

    public static void flingAwayFromEntity(Entity target, Entity repulser, double forceMultiplier) {
        if (target.hasVehicle()) {
            target.stopRiding();
        }

        Vec3d velocity = target.getVelocity();
        double vx = velocity.x;
        double vz = velocity.z;

        double dx = target.getX() - repulser.getX();
        double dz = target.getZ() - repulser.getZ();
        double flatDistSq = dx * dx + dz * dz;

        if (flatDistSq > 0.1) {
            double flatDist = Math.sqrt(flatDistSq);
            vx += (dx / flatDist) * 0.5 * forceMultiplier;
            vz += (dz / flatDist) * 0.5 * forceMultiplier;
        }

        double vy = velocity.y + 0.25 * forceMultiplier;

        vx *= target.getRandom().nextDouble() * 0.2 + 0.9;
        vz *= target.getRandom().nextDouble() * 0.2 + 0.9;

        target.setVelocity(
                MathHelper.clamp(vx, -1.0, 1.0),
                Math.min(vy, 0.75),
                MathHelper.clamp(vz, -1.0, 1.0));

        target.velocityDirty = true;
        target.velocityModified = true;
    }

    private KickLogic() {}
}