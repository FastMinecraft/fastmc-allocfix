package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.BlockCullingCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Block.class)
public class MixinBlock {
    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    @Environment(value = EnvType.CLIENT)
    public static boolean shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction facing) {
        return BlockCullingCache.getInstance().shouldDrawSide(
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            state,
            facing
        );
    }
}
