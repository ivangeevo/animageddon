package org.ivangeevo.animageddon.entity.interfaces;

import org.ivangeevo.animageddon.util.MiscUtils;

public interface CowEntityAdded {

    int KICK_ATTACK_DURATION = 20;
    int KICK_ATTACK_TICKS_TO_COOLDOWN = 40;
    int FULL_MILK_ACCUMULATION_COUNT = MiscUtils.TICKS_PER_GAME_DAY;
    int kickAttackInProgressCounter();
    int kickAttackLegUsed();

    void setKickAttackInProgressCounter(int Value);
    void setKickAttackLegUsed(int value);
    void onClientNotifiedOfKickAttack();

    double KICK_ATTACK_RANGE = 1.75D;
    double KICK_ATTACK_TIP_COLLISION_WIDTH = 2.75D;
    double KICK_ATTACK_TIP_COLLISION_HALF_WIDTH = (KICK_ATTACK_TIP_COLLISION_WIDTH / 2D );
    double KICK_ATTACK_TIP_COLLISION_HEIGHT = 2D;
    double KICK_ATTACK_TIP_COLLISION_HALF_HEIGHT = (KICK_ATTACK_TIP_COLLISION_HEIGHT / 2D );

}