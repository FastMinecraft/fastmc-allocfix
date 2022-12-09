package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.IPatchedIBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkCache.class)
public class MixinChunkCache implements IPatchedIBlockAccess {
    @Shadow
    protected int chunkX;

    @Shadow
    protected int chunkZ;

    @Shadow
    protected Chunk[][] chunkArray;

    @Override
    public @NotNull IBlockState getBlockState(int x, int y, int z) {
        if (y >= 0 && y < 256) {
            int i = (x >> 4) - this.chunkX;
            int j = (z >> 4) - this.chunkZ;

            if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
                Chunk chunk = this.chunkArray[i][j];

                if (chunk != null) {
                    return chunk.getBlockState(x, y, z);
                }
            }
        }

        return Blocks.AIR.getDefaultState();
    }
}
