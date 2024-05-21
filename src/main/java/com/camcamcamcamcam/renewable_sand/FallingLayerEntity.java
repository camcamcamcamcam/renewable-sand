package com.camcamcamcamcam.renewable_sand;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import static com.camcamcamcamcam.renewable_sand.FallingLayerBlock.WATERLOGGED;
import static net.minecraft.world.level.block.SnowLayerBlock.LAYERS;

public class FallingLayerEntity extends FallingBlockEntity {

    public FallingLayerEntity(EntityType<? extends FallingBlockEntity> p_31950_, Level p_31951_) {
        super(p_31950_, p_31951_);
    }

    public FallingLayerEntity(Level p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_) {
        super(p_31953_, p_31954_, p_31955_, p_31956_, p_31957_);
    }

    public static FallingLayerEntity fall(Level p_201972_, BlockPos p_201973_, BlockState p_201974_) {
        FallingLayerEntity fallinglayerentity = new FallingLayerEntity(p_201972_, (double)p_201973_.getX() + 0.5D, (double)p_201973_.getY(), (double)p_201973_.getZ() + 0.5D, p_201974_.hasProperty(BlockStateProperties.WATERLOGGED) ? p_201974_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : p_201974_);
        p_201972_.setBlock(p_201973_, p_201974_.getFluidState().createLegacyBlock(), 3);
        p_201972_.addFreshEntity(fallinglayerentity);
        return fallinglayerentity;
    }

    @Override
    public void callOnBrokenAfterFall(Block fallingBlock, BlockPos pos) {
        Level level = this.level();
        BlockState newState = level.getBlockState(pos);
        if (newState.getBlock().equals(fallingBlock)) {
            int layerCount = this.getBlockState().getValue(LAYERS) + newState.getValue(LAYERS);
            if (layerCount > 8) {
                if (FallingBlock.isFree(level.getBlockState(pos.above()))) {
                    boolean isWater = level.getBlockState(pos.above()).getFluidState().is(Fluids.WATER);
                    level.setBlock(pos.above(), this.getBlockState().setValue(LAYERS, layerCount - 8).setValue(WATERLOGGED, isWater), Block.UPDATE_ALL);
                }
                layerCount = 8;
            }
            level.setBlock(pos, newState.setValue(LAYERS, layerCount), Block.UPDATE_ALL);

        } else if (fallingBlock instanceof Fallable) {
            ((Fallable)fallingBlock).onBrokenAfterFall(level, pos, this);
        }

    }

    @Override
    public ItemEntity spawnAtLocation(ItemLike p_19999_) {
        if (this.getBlockState().getValue(LAYERS) != 8) {
            return null;
        }
        if (p_19999_.asItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof FallingLayerBlock block) {
                return this.spawnAtLocation(block.fullBlock, 0);
            }
        }
        return null;
    }
}
