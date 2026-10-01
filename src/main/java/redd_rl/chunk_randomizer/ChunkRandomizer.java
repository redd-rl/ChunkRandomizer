package redd_rl.chunk_randomizer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class ChunkRandomizer implements ModInitializer {
    int totalRandomBlocks = 3;
    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel level = player.level();
                ChunkPos currentChunk = player.chunkPosition();

                RandomizedChunkStateTracker state = level.getDataStorage().computeIfAbsent(RandomizedChunkStateTracker.TYPE);

                if (!state.isRandomized(currentChunk)) {
                    state.markRandomized(currentChunk);
                    randomizeChunk(level, currentChunk);
                }
            }
        });
    }

    private Block fetchRandomBlock(ServerLevel level) {
        Block randomBlock = BuiltInRegistries.BLOCK.getRandom(level.getRandom())
                .map(Holder::value)
                .orElse(Blocks.DIRT); // intellij i don't care that i can just return this sthis looks NICER!
        return randomBlock;
    }

    private void randomizeChunk(ServerLevel level, ChunkPos chunkPos) {
        ArrayList<Block> randomBlocks = new ArrayList<Block>();

        for (int i = 0; i < this.totalRandomBlocks; i++) {
            randomBlocks.add(fetchRandomBlock(level));
        }

        ArrayList<BlockState> randomBlockStates = new ArrayList<BlockState>();

        for (Block randomBlock : randomBlocks) {
            randomBlockStates.add(randomBlock.defaultBlockState());
        }

        int startX = chunkPos.getMinBlockX();
        int startZ = chunkPos.getMinBlockZ();
        int minY = level.getMinY();
        int maxY = level.getMaxY();

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    mutable.set(startX + x, y, startZ + z);

                    if (
                            !(
                                    level.getBlockState(mutable).isAir() ||
                                    level.getBlockState(mutable).is(Blocks.BEDROCK) ||
                                    level.getBlockState(mutable).is(Blocks.OBSIDIAN) ||
                                    level.getBlockState(mutable).is(Blocks.END_PORTAL_FRAME) // this makes sense to me!
                            )) {

                        int randomStateIndex = (int)(Math.random() * randomBlockStates.size());
                        BlockState stateToSet = randomBlockStates.get(randomStateIndex); // haha statesetter!! rlgym reference??
                        level.setBlock(mutable, stateToSet, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                    }
                }
            }
        }
    }
}