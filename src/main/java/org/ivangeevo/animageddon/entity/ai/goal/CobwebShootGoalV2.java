package org.ivangeevo.animageddon.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import org.ivangeevo.animageddon.entity.interfaces.SpiderEntityAdded;

import java.util.EnumSet;

public class CobwebShootGoalV2<T extends HostileEntity> extends Goal {
    private final T actor;
    private int cooldown = -1;

    public CobwebShootGoalV2(T actor) {
        this.actor = actor;
        this.setControls(EnumSet.noneOf(Goal.Control.class));
    }

    @Override
    public boolean canStart()
    {
        return this.actor.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    @Override
    public void tick()
    {
        LivingEntity targetEntity = this.actor.getTarget();
        if (targetEntity == null) {
            return;
        }

        if (this.cooldown <= 0)
        {
            spitWeb(targetEntity);
            this.cooldown = 0; // Prevent continuous shooting
        } else {
            this.cooldown--;
        }
    }

    private void spitWeb(LivingEntity targetEntity)
    {
        if (this.actor instanceof SpiderEntity spider)
        {
            if (((SpiderEntityAdded)spider).hasWeb())
            {
                ((SpiderEntityAdded)spider).spitWeb(targetEntity);
            }
        }
    }
}
