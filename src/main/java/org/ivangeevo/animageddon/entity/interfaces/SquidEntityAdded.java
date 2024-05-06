package org.ivangeevo.animageddon.entity.interfaces;

public interface SquidEntityAdded {

    int tentacleAttackInProgressCounter();

    void setTentacleAttackInProgressCounter(int value);

    void onClientNotifiedOfTentacleAttack(double dTargetX, double dTargetY, double dTargetZ);



}
