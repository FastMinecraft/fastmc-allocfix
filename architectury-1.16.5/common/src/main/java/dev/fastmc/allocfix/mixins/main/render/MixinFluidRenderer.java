package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.BlockCullingCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {
    private static final VoxelShape[] cuboidCache = new VoxelShape[201];

    static {
        for (int i = 0; i < cuboidCache.length; i++) {
            cuboidCache[i] = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, i * 0.005, 1.0);
        }
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private static boolean method_29710(
        BlockView blockView,
        Direction direction,
        float maxDeviation,
        BlockPos blockPos,
        BlockState blockState
    ) {
        if (blockState.isOpaque()) {
            VoxelShape a = cuboidCache[(int) (maxDeviation * 200.0f)];
            VoxelShape b = blockState.getCullingShape(blockView, blockPos);
            return BlockCullingCache.getInstance().isSideCovered(a, b, direction);
        }
        return false;
    }
}
