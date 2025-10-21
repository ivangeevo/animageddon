package org.ivangeevo.animageddon.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.block.ModBlocks;
import org.spongepowered.asm.mixin.Unique;

public class ModPlayerBlockBreakEvents {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.isOf(Blocks.COBWEB)) {
                // Run your own logic instead of vanilla
                if (!world.isClient()) {
                    boolean breaksFully = player.getMainHandStack().isIn(ConventionalItemTags.SHEAR_TOOLS);

                    if (breaksFully) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), Items.COBWEB.getDefaultStack());
                    } else {
                        world.setBlockState(pos, ModBlocks.WEB_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
                    }

                    player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
                    player.addExhaustion(0.005f);
                }

                // cancel original break
                return false;
            }

            // allow vanilla handling for everything else
            return true;
        });

    }

    @Unique
    private static void afterBreakCobweb(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        // if shears item drop the whole block
        // if another tool that's set viable for the block - break in stages (like swords and chisels)
        if (!world.isClient()) {
            player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
            player.addExhaustion(0.005f);

            boolean breaksFully = player.getMainHandStack().isIn(ConventionalItemTags.SHEAR_TOOLS);

            if (breaksFully) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), Items.COBWEB.getDefaultStack());
            } else {
                world.setBlockState(pos, ModBlocks.WEB_BLOCK.getDefaultState(),4,0);
            }
        }
    }
}
