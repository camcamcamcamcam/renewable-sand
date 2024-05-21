package com.camcamcamcamcam.renewable_sand;

import com.camcamcamcamcam.renewable_sand.FallingLayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FallingLayerBlock extends SnowLayerBlock implements SimpleWaterloggedBlock, Fallable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public final Block fullBlock;
    public FallingLayerBlock(Block fullBlock, Properties properties) {
        super(properties);
        this.fullBlock = fullBlock;
        registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(LAYERS, 1));
    }

    @Override
    public void onPlace(BlockState p_53233_, Level p_53234_, BlockPos p_53235_, BlockState p_53236_, boolean p_53237_) {
        p_53234_.scheduleTick(p_53235_, this, this.getDelayAfterPlace());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState p_53226_, Direction p_53227_, BlockState p_53228_, LevelAccessor p_53229_, BlockPos p_53230_, BlockPos p_53231_) {
        if (p_53226_.getValue(WATERLOGGED)) {
            p_53229_.scheduleTick(p_53230_, Fluids.WATER, Fluids.WATER.getTickDelay(p_53229_));
        }
        p_53229_.scheduleTick(p_53230_, this, this.getDelayAfterPlace());
        return super.updateShape(p_53226_, p_53227_, p_53228_, p_53229_, p_53230_, p_53231_);
    }

    @Override
    public void tick(BlockState p_221124_, ServerLevel p_221125_, BlockPos p_221126_, RandomSource p_221127_) {
        if (isFree(p_221124_, p_221125_.getBlockState(p_221126_.below())) && p_221126_.getY() >= p_221125_.getMinBuildHeight()) {
            FallingLayerEntity fallinglayerentity = FallingLayerEntity.fall(p_221125_, p_221126_, p_221124_);
        }
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState p_221124_, BlockState p_53242_) {
        return (p_53242_.getBlock() == p_221124_.getBlock() && p_53242_.getValue(LAYERS) < 8) || FallingBlock.isFree(p_53242_);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context)
                .setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
    }

    @Override
    public boolean canSurvive(BlockState p_56602_, LevelReader p_56603_, BlockPos p_56604_) {
        return true;
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState newState, BlockState oldState, FallingBlockEntity fallingBlock) {
        if (oldState.getFluidState().is(Fluids.WATER)) {
            level.setBlock(pos, newState.setValue(WATERLOGGED, true), Block.UPDATE_ALL);
        }
    }

    @Override
    public void randomTick(BlockState p_222448_, ServerLevel p_222449_, BlockPos p_222450_, RandomSource p_222451_) {
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }
}
