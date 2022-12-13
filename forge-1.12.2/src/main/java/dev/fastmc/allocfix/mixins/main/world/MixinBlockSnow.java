package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.IPatchedIBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static net.minecraft.block.BlockSnow.LAYERS;

@Mixin(BlockSnow.class)
public class MixinBlockSnow extends Block {
    public MixinBlockSnow(Material materialIn) {
        super(materialIn);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("deprecation")
    @Overwrite
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(
        @NotNull IBlockState blockState,
        @NotNull IBlockAccess blockAccess,
        @NotNull BlockPos pos,
        @NotNull EnumFacing side
    ) {
        if (side == EnumFacing.UP) {
            return true;
        } else {
            IBlockState stateUp = ((IPatchedIBlockAccess) blockAccess).getBlockState(
                pos.getX() + side.getXOffset(),
                pos.getY() + side.getYOffset(),
                pos.getZ() + side.getZOffset()
            );
            return (stateUp.getBlock() != this || stateUp.getValue(LAYERS) < blockState.getValue(LAYERS))
                && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
        }
    }
}
