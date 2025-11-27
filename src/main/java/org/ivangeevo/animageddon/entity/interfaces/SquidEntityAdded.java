package org.ivangeevo.animageddon.entity.interfaces;

public interface SquidEntityAdded {

    int animageddon$tentacleAttackInProgressCounter();

    void animageddon$setTentacleAttackInProgressCounter(int value);

    void animageddon$onClientNotifiedOfTentacleAttack(double dTargetX, double dTargetY, double dTargetZ);

}