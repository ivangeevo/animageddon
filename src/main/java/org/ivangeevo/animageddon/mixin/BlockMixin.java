package org.ivangeevo.animageddon.mixin;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock {

    public BlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void onAfterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (state.isOf(Blocks.COBWEB)) {
            this.afterBreakCobweb(world, player, pos);
            ci.cancel();
        }
    }

    @Unique
    private void afterBreakCobweb(World world, PlayerEntity player, BlockPos pos) {
        // if shears item drop the whole block
        // if another tool that's set viable for the block - break in stages (like swords and chisels)
        if (!world.isClient()) {
            player.incrementStat(Stats.MINED.getOrCreateStat((Block)(Object)this));
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