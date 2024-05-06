package org.ivangeevo.animageddon.entity.interfaces;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.CowEntity;
import org.ivangeevo.animageddon.util.MiscUtils;

public interface CowEntityAdded {


    TrackedData<Boolean> GOT_MILK = DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    TrackedData<Byte> HUNGER_LEVEL = DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.BYTE);
    TrackedData<Boolean> IN_LOVE = DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    TrackedData<Boolean> WEARING_BREEDING_HARNESS = DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    int KICK_ATTACK_DURATION = 20;
    int KICK_ATTACK_TICKS_TO_COOLDOWN = 40;
    int FULL_MILK_ACCUMULATION_COUNT = MiscUtils.TICKS_PER_GAME_DAY;
    int kickAttackInProgressCounter();
    int kickAttackLegUsed();

    void setKickAttackInProgressCounter(int Value);
    void setKickAttackLegUsed(int value);
    void setGotMilk(boolean bGotMilk);
    void onClientNotifiedOfKickAttack();

    boolean gotMilk();

    double KICK_ATTACK_RANGE = 1.75D;
    double KICK_ATTACK_TIP_COLLISION_WIDTH = 2.75D;
    double KICK_ATTACK_TIP_COLLISION_HALF_WIDTH = (KICK_ATTACK_TIP_COLLISION_WIDTH / 2D );
    double KICK_ATTACK_TIP_COLLISION_HEIGHT = 2D;
    double KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT = (KICK_ATTACK_TIP_COLLISION_HEIGHT / 2D );



}
