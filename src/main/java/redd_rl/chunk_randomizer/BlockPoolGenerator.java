package redd_rl.chunk_randomizer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BlockPoolGenerator {

    public static List<Block> createSafeBlockPool(LevelReader level) {
        List<Block> safeBlocks = new ArrayList<>();

        for (Block block : BuiltInRegistries.BLOCK) {

            if (block.defaultBlockState().isAir()) continue;

            if (block instanceof FallingBlock) continue;

            if (block instanceof PressurePlateBlock) continue;
            if (block instanceof WeightedPressurePlateBlock) continue;

            if (block instanceof SimpleWaterloggedBlock) continue;
            if (block instanceof WaterloggedTransparentBlock) continue;
            if (block instanceof LiquidBlock) continue;

            if (block instanceof WallBannerBlock) continue;
            if (block instanceof BannerBlock) continue;

            if (block instanceof SignBlock) continue;
            if (block instanceof WallSignBlock) continue;

            if (block instanceof RedstoneWireBlock) continue;
            if (block instanceof RepeaterBlock) continue;
            if (block instanceof ComparatorBlock) continue;
            if (block instanceof ButtonBlock) continue;

            if (block instanceof CaveVinesBlock) continue;
            if (block instanceof TwistingVinesBlock) continue;
            if (block instanceof TwistingVinesPlantBlock) continue;
            if (block instanceof WeepingVinesBlock) continue;
            if (block instanceof WeepingVinesPlantBlock) continue;
            if (block instanceof VineBlock) continue;
            if (block instanceof FlowerPotBlock) continue;
            if (block instanceof GlowLichenBlock) continue;
            if (block instanceof SweetBerryBushBlock) continue;
            if (block instanceof CaveVines) continue;
            if (block instanceof SkullBlock) continue;
            if (block instanceof WallSkullBlock) continue;
            if (block instanceof TorchBlock) continue;
            if (block instanceof WallTorchBlock) continue;
            if (block instanceof RedstoneTorchBlock) continue;
            if (block instanceof RedstoneWallTorchBlock) continue;
            if (block instanceof EnchantingTableBlock) continue;
            if (block instanceof SnowLayerBlock) continue;
            if (block instanceof CarpetBlock) continue;
            if (block instanceof MossyCarpetBlock) continue;
            if (block instanceof ShelfMushroomBlock) continue;
            if (block instanceof BrushableBlock) continue;
            if (block instanceof MushroomBlock) continue;
            if (block instanceof TripWireHookBlock) continue;

            if (block instanceof LiquidBlock || block instanceof CommandBlock || block instanceof StructureBlock) continue;

            BlockState defaultState = block.defaultBlockState();
            BlockPos testPos = new BlockPos(0, 1, 0);

            if (level != null && !defaultState.canSurvive(level, testPos)) {
                continue;
            }

            if (defaultState.is(BlockTags.CROPS) ||
                    defaultState.is(BlockTags.FLOWERS) ||
                    defaultState.is(BlockTags.SAPLINGS) ||
                    block instanceof TorchBlock ||
                    block instanceof DoorBlock) {
                continue;
            }

            safeBlocks.add(block);
        }

        return safeBlocks;
    }
}