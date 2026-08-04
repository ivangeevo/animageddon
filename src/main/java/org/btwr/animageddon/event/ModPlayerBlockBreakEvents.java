package org.btwr.animageddon.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.animageddon.block.ModBlocks;
import org.spongepowered.asm.mixin.Unique;

public class ModPlayerBlockBreakEvents {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(ModPlayerBlockBreakEvents::afterBreakCobweb);
    }

    @Unique
    private static void afterBreakCobweb(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
        // shears drop the whole block
        // if another tool that's set viable for the block - break in stages (like swords and chisels)
        if (!world.isClient()) {
            player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
            player.addExhaustion(0.005f);

            ItemStack stack = player.getMainHandStack();

            boolean isUsingShears = stack.isIn(ConventionalItemTags.SHEAR_TOOLS);

            if (isUsingShears) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), Items.COBWEB.getDefaultStack());
            } else {
                world.setBlockState(pos, ModBlocks.WEB_BLOCK.getDefaultState(),4,0);
            }
        }
    }

}