package org.animageddon.entity.interfaces;

import net.minecraft.entity.passive.CowEntity;

public interface EntityAdded {

    void onKickedByCow(CowEntity cow);

    void onClientNotifiedOfKickAttack();

}
