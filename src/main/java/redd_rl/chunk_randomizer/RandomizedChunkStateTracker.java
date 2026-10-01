package redd_rl.chunk_randomizer;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RandomizedChunkStateTracker extends SavedData {
    private final Set<Long> processedChunks = new HashSet<>();

    public boolean isRandomized(ChunkPos pos) {
        return processedChunks.contains(pos.pack());
    }

    public void markRandomized(ChunkPos pos) {
        if (processedChunks.add(pos.pack())) {
            this.setDirty();
        }
    }

    public static final Codec<RandomizedChunkStateTracker> CODEC = Codec.LONG.listOf().xmap(
            list -> {
                RandomizedChunkStateTracker state = new RandomizedChunkStateTracker();
                state.processedChunks.addAll(list);
                return state;
            },
            state -> new ArrayList<>(state.processedChunks)
    );

    public static final SavedDataType<RandomizedChunkStateTracker> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("chunkrandomizer", "randomized_chunks"),
            RandomizedChunkStateTracker::new,
            CODEC,
            null
    );
}