package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.IPatchedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class MixinWorld implements IPatchedBlockView {
    @Shadow public abstract WorldChunk getChunk(int par1, int par2);

    @NotNull
    @Override
    public BlockState getBlockState(int x, int y, int z) {
        if (World.isOutOfBuildLimitVertically(y)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        WorldChunk worldChunk = this.getChunk(x >> 4, z >> 4);
        return ((IPatchedBlockView) worldChunk).getBlockState(x, y, z);
    }
}
