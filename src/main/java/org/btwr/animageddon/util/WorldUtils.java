package org.btwr.animageddon.util;

import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtils {

    public static boolean isWaterSourceBlock(World world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        return fluidState.isIn(FluidTags.WATER) && fluidState.isStill();
    }

}