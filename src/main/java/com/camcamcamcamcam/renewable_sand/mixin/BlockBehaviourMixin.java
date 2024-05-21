package com.camcamcamcamcam.renewable_sand.mixin;

import com.camcamcamcamcam.renewable_sand.FallingLayerEntity;
import com.camcamcamcamcam.renewable_sand.RenewableSand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {

	@Inject(at = @At("HEAD"), method = "randomTick")
	private void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo info) {
		if (state.getBlock() instanceof BubbleColumnBlock) {
			System.out.println("Mixin running");
			System.out.println("ticking? " + Blocks.BUBBLE_COLUMN.isRandomlyTicking);
			BlockPos abovePos = pos.above();
			Block aboveBlock = world.getBlockState(abovePos).getBlock();
			boolean noFallingBlockAbove = world.getEntitiesOfClass(FallingBlockEntity.class, new AABB(pos)).isEmpty();

			if (noFallingBlockAbove) {
				RenewableSand.SAND_FALLABLES.forEach((inputBlock, outputBlock) -> {
					System.out.println(inputBlock.get() + " " + aboveBlock);
					System.out.println(inputBlock.get() == aboveBlock);
					if (inputBlock.get() == aboveBlock) {
						this.spawnFallingBlock(world, pos, outputBlock.get());
					}
				});
				/*
				if (RenewableSand.ATMOSPHERIC_SAND_FALLABLES != null) {
					RenewableSand.ATMOSPHERIC_SAND_FALLABLES.forEach((inputBlock, outputBlock) -> {
						if (inputBlock.get() == aboveBlock) {
							this.spawnFallingBlock(world, pos, outputBlock.get());
						}
					});
				}
				*/
				RenewableSand.GRAVEL_FALLABLES.forEach((inputBlock, outputBlock) -> {
					if (inputBlock.get() == aboveBlock) {
						this.spawnFallingBlock(world, pos, outputBlock.get());
					}
				});
			}
		}
	}

	private void spawnFallingBlock(ServerLevel world, BlockPos pos, Block block) {
		System.out.println("Spawning " + block);
		BlockState state = block.defaultBlockState();
		FallingLayerEntity fallinglayerentity = new FallingLayerEntity(world,
				(double)pos.getX() + 0.5D, (double)pos.above().getY() - 0.125D, (double)pos.getZ() + 0.5D,
				state.hasProperty(BlockStateProperties.WATERLOGGED) ?
						state.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) : state);
		fallinglayerentity.time = 1;
		world.addFreshEntity(fallinglayerentity);
	}
}