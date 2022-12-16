package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedIBlockAccess;
import dev.fastmc.allocfix.IPatchedIBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MapColor;
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

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid extends Block {
    public MixinBlockLiquid(
        Material blockMaterialIn,
        MapColor blockMapColorIn
    ) {
        super(blockMaterialIn, blockMapColorIn);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    @Overwrite
    public boolean shouldSideBeRendered(
        @NotNull IBlockState blockState,
        @NotNull IBlockAccess blockAccess,
        @NotNull BlockPos pos,
        @NotNull EnumFacing side
    ) {
        if (blockAccess instanceof IPatchedIBlockAccess) {
            if (((IPatchedIBlockAccess) blockAccess).getBlockState(
                pos.getX() + side.getXOffset(),
                pos.getY() + side.getYOffset(),
                pos.getZ() + side.getZOffset()
            ).getMaterial() == this.material) {
                return false;
            }
        } else {
            if (blockAccess.getBlockState(pos.offset(side)).getMaterial() == this.material) {
                return false;
            }
        }

        return side == EnumFacing.UP || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getPackedLightmapCoords(@NotNull IBlockState state, IBlockAccess source, @NotNull BlockPos pos) {
        int lightThis = source.getCombinedLight(pos, 0);
        int lightUp = ((IPatchedIBlockAccess) source).getCombinedLight(pos.getX(), pos.getY() + 1, pos.getZ(), 0);
        int blockLightThis = lightThis & 255;
        int blockLightUp = lightUp & 255;
        int skyLightThis = lightThis >> 16 & 255;
        int skyLightUp = lightUp >> 16 & 255;
        return Math.max(blockLightThis, blockLightUp) | Math.max(skyLightThis, skyLightUp) << 16;
    }
}
