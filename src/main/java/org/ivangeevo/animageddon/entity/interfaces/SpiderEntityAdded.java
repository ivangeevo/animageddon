package org.ivangeevo.animageddon.entity.interfaces;

import net.minecraft.entity.Entity;

public interface SpiderEntityAdded
{

     int timeToNextWeb = 0;

    void setShooting(boolean shooting);

    int getTimeToNextWeb();

    void setTimeToNextWeb(int timeToNextWeb);

    boolean hasWeb();

    void spitWeb(Entity targetEntity);




}

