package redd_rl.chunk_randomizer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;

public class ChunkRandomizer implements ModInitializer {
    int totalRandomBlocks = 1;

    private List<Block> safeBlockPool = null;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel level = player.level();
                ChunkPos currentChunk = player.chunkPosition();

                GlobalPos spawnPos = level.getRespawnData().globalPos();
                Vec3i spawnPosVec = new Vec3i(spawnPos.pos().getX(), spawnPos.pos().getY(), spawnPos.pos().getZ());

                int spawnRadius = level.getGameRules().get(GameRules.RESPAWN_RADIUS);

                RandomizedChunkStateTracker state = level.getDataStorage().computeIfAbsent(RandomizedChunkStateTracker.TYPE);

                if (player.blockPosition().distSqr(spawnPosVec) < spawnRadius * spawnRadius) {
                    if (!state.isRandomized(currentChunk)) {
                        state.markRandomized(currentChunk);
                    }
                    continue;
                }

                if (!state.isRandomized(currentChunk)) {
                    state.markRandomized(currentChunk);
                    randomizeChunk(level, currentChunk);
                }
            }
        });
    }

    private Block fetchRandomBlock(ServerLevel level) {
        if (this.safeBlockPool == null) {
            this.safeBlockPool = BlockPoolGenerator.createSafeBlockPool(level);
        }

        if (this.safeBlockPool.isEmpty()) {
            return Blocks.DIRT;
        }
        int randomIndex = level.getRandom().nextInt(this.safeBlockPool.size());
        return this.safeBlockPool.get(randomIndex);
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