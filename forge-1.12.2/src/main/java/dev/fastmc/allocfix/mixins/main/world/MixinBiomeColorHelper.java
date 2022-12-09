package dev.fastmc.allocfix.mixins.main.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BiomeColorHelper.class)
public class MixinBiomeColorHelper {
    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private static int getColorAtPos(
        IBlockAccess blockAccess,
        BlockPos pos,
        BiomeColorHelper.ColorResolver colorResolver
    ) {
        int r = 0;
        int g = 0;
        int b = 0;

        BlockPos.PooledMutableBlockPos blockPos = BlockPos.PooledMutableBlockPos.retain();

        int x1 = pos.getX();
        int z1 = pos.getZ();
        int y = pos.getY();

        for (int x = x1 - 1; x <= x1 + 1; ++x) {
            for (int z = z1 - 1; z <= z1 + 1; ++z) {
                blockPos.setPos(x, y, z);
                int c = colorResolver.getColorAtPos(blockAccess.getBiome(blockPos), blockPos);
                r += (c & 0xFF0000) >> 16;
                g += (c & 0x00FF00) >> 8;
                b += c & 0x0000FF;
            }
        }

        blockPos.release();

        return (r / 9 & 0xFF) << 16 | (g / 9 & 0xFF) << 8 | b / 9 & 0xFF;
    }
}
