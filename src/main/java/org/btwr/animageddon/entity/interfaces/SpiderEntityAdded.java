package org.btwr.animageddon.entity.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SpiderEntity;

public interface SpiderEntityAdded
{

     int timeToNextWeb = 0;

    void setShooting(boolean shooting);

    int getTimeToNextWeb();

    void setTimeToNextWeb(int timeToNextWeb);

    boolean hasWeb();

    void spitWeb(SpiderEntity spiderEntity, Entity targetEntity);

}

