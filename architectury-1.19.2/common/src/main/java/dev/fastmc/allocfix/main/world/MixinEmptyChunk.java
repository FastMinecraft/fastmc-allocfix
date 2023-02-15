package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedBlockView;
import dev.fastmc.allocfix.IPatchedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EmptyChunk.class)
public class MixinEmptyChunk implements IPatchedBlockView {
    @NotNull
    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return Blocks.VOID_AIR.getDefaultState();
    }
}
