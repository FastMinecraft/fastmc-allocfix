package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.IPatchedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkRendererRegion.class)
public abstract class MixinChunkRendererRegion implements IPatchedBlockView {
    @Shadow
    @Final
    protected BlockState[] blockStates;

    @Shadow
    protected abstract int getIndex(int x, int y, int z);

    @NotNull
    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.blockStates[this.getIndex(x, y, z)];
    }
}
