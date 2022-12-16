package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedChunk;
import dev.fastmc.allocfix.IPatchedIBlockAccess;
import dev.fastmc.allocfix.IPatchedChunk;
import dev.fastmc.allocfix.IPatchedIBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkCache.class)
public abstract class MixinChunkCache implements IPatchedIBlockAccess {
    @Shadow(aliases = "field_72818_a")
    protected int chunkX;

    @Shadow(aliases = "field_72816_b")
    protected int chunkZ;

    @Shadow(aliases = "field_72817_c")
    protected Chunk[][] chunkArray;

    @Shadow
    protected World world;

    @Shadow
    public abstract IBlockState getBlockState(BlockPos pos);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow(remap = false)
    protected abstract boolean withinBounds(int x, int z);

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

    private static final EnumFacing[] FACING = EnumFacing.values();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Overwrite
    @SideOnly(Side.CLIENT)
    private int getLightForExt(EnumSkyBlock type, BlockPos pos) {
        if (type == EnumSkyBlock.SKY && !this.world.provider.hasSkyLight()) {
            return 0;
        } else if (pos.getY() >= 0 && pos.getY() < 256) {
            if (this.getBlockState(pos).useNeighborBrightness()) {
                int maxLight = 0;

                for (int i = 0, facingLength = FACING.length; i < facingLength; i++) {
                    EnumFacing facing = FACING[i];
                    int light = this.getLightFor(
                        type,
                        pos.getX() + facing.getXOffset(),
                        pos.getY() + facing.getYOffset(),
                        pos.getZ() + facing.getZOffset()
                    );

                    if (light > maxLight) {
                        maxLight = light;
                    }

                    if (maxLight >= 15) {
                        return maxLight;
                    }
                }

                return maxLight;
            } else {
                int chunkX = (pos.getX() >> 4) - this.chunkX;
                int chunkZ = (pos.getZ() >> 4) - this.chunkZ;
                if (!withinBounds(chunkX, chunkZ)) return type.defaultLightValue;
                return this.chunkArray[chunkX][chunkZ].getLightFor(type, pos);
            }
        } else {
            return type.defaultLightValue;
        }
    }

    private int getLightForExt(EnumSkyBlock type, int x, int y, int z) {
        if (type == EnumSkyBlock.SKY && !this.world.provider.hasSkyLight()) {
            return 0;
        } else {
            if (y >= 0 && y < 256) {
                if (this.getBlockState(x, y, z).useNeighborBrightness()) {
                    int maxLight = 0;

                    for (int i = 0, facingLength = FACING.length; i < facingLength; i++) {
                        EnumFacing facing = FACING[i];
                        int light = this.getLightFor(
                            type,
                            x + facing.getXOffset(),
                            y + facing.getYOffset(),
                            z + facing.getZOffset()
                        );

                        if (light > maxLight) {
                            maxLight = light;
                        }

                        if (maxLight >= 15) {
                            return maxLight;
                        }
                    }

                    return maxLight;
                } else {
                    int chunkX = (x >> 4) - this.chunkX;
                    int chunkZ = (z >> 4) - this.chunkZ;
                    if (!withinBounds(chunkX, chunkZ)) return type.defaultLightValue;
                    return ((IPatchedChunk) this.chunkArray[chunkX][chunkZ]).getLightFor(type, x, y, z);
                }
            } else {
                return type.defaultLightValue;
            }
        }
    }

    public int getLightFor(EnumSkyBlock type, int x, int y, int z) {
        if (y >= 0 && y < 256) {
            int chunkX = (x >> 4) - this.chunkX;
            int chunkZ = (z >> 4) - this.chunkZ;
            if (!withinBounds(chunkX, chunkZ)) return type.defaultLightValue;
            return ((IPatchedChunk) this.chunkArray[chunkX][chunkZ]).getLightFor(type, x, y, z);
        } else {
            return type.defaultLightValue;
        }
    }

    @Override
    public int getCombinedLight(int x, int y, int z, int lightValue) {
        int skyLight = this.getLightForExt(EnumSkyBlock.SKY, x, y, z);
        int blockLight = this.getLightForExt(EnumSkyBlock.BLOCK, x, y, z);

        if (blockLight < lightValue) {
            blockLight = lightValue;
        }

        return skyLight << 20 | blockLight << 4;
    }
}
