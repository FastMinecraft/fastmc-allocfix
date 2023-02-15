package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedBlockView;
import dev.fastmc.allocfix.IPatchedRenderedChunk;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkRendererRegion.class)
public abstract class MixinChunkRendererRegion implements IPatchedBlockView {

    @Shadow @Final private int chunkXOffset;

    @Shadow @Final private int chunkZOffset;

    @Shadow @Final protected RenderedChunk[][] chunks;

    @NotNull
    @Override
    public BlockState getBlockState(int x, int y, int z) {
        int i = ChunkSectionPos.getSectionCoord(x) - this.chunkXOffset;
        int j = ChunkSectionPos.getSectionCoord(z) - this.chunkZOffset;
        return ((IPatchedRenderedChunk) this.chunks[i][j]).getBlockState(x, y, z);
    }
}
