package org.ivangeevo.animageddon.block.blocks;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.animageddon.block.ModBlocks;
import org.ivangeevo.animageddon.item.ModItems;
import org.jetbrains.annotations.Nullable;

import static btwr.btwrsl.tag.BTWRConventionalTags.Items.MODERN_CHISELS;
import static btwr.btwrsl.tag.BTWRConventionalTags.Items.PRIMITIVE_CHISELS;

public class WebBlock extends CobwebBlock
{
    public static final MapCodec<CobwebBlock> CODEC = WebBlock.createCodec(WebBlock::new);

    @Override
    public MapCodec<CobwebBlock> getCodec() {
        return CODEC;
    }

    public WebBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(BREAK_LEVEL, 0));
    }

    public static final IntProperty BREAK_LEVEL = IntProperty.of("break_level", 0, 2);

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BREAK_LEVEL);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(BREAK_LEVEL, 0);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (player.getMainHandStack() != null) {

            if (isSuitableChiselForState(state, tool) ) {

                if (state.get(BREAK_LEVEL) < 1) {
                    changeState(1, world, player, pos);
                } else if (state.get(BREAK_LEVEL) == 1) {
                    changeState(2, world, player, pos);
                } else {
                    world.removeBlock(pos, false);
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), Items.STRING.getDefaultStack());
                }

            } else if (tool.isIn(ConventionalItemTags.SHEAR_TOOLS)) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ModBlocks.WEB_BLOCK.asItem().getDefaultStack());
            }
        }


    }

    private void changeState(int breakLevel, World world, PlayerEntity player, BlockPos pos) {
        if (!world.isClient()) {
            player.incrementStat(Stats.MINED.getOrCreateStat((Block)(Object)this));
            player.addExhaustion(0.005f);
            world.setBlockState(pos, ModBlocks.WEB_BLOCK.getDefaultState().with(BREAK_LEVEL, breakLevel),4,0);
        }
    }

    private boolean isSuitableChiselForState(BlockState state,ItemStack stack) {
        return ( stack.isIn(PRIMITIVE_CHISELS) || stack.isIn(MODERN_CHISELS) )
                && state.isIn(BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    }
}
