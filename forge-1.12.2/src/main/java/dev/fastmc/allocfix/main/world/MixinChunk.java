package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedChunk;
import dev.fastmc.allocfix.IPatchedWorld;
import dev.fastmc.allocfix.IPatchedChunk;
import dev.fastmc.allocfix.IPatchedWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;

@Mixin(Chunk.class)
public abstract class MixinChunk implements IPatchedChunk {
    @Shadow
    @Final
    private ExtendedBlockStorage[] storageArrays;

    @Shadow
    @Final
    private int[] heightMap;

    @Shadow
    @Final
    private World world;

    @Shadow
    private boolean dirty;

    @Shadow public abstract IBlockState getBlockState(int x, int y, int z);

    @Shadow private boolean loaded;

    @Shadow @Final public int x;

    @Shadow @Final public int z;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private void updateSkylightNeighborHeight(int x, int z, int startY, int endY) {
        IPatchedWorld patchedWorld = (IPatchedWorld) this.world;
        if (endY > startY && patchedWorld.isAreaLoaded(x, 0, z, 16, true)) {
            for (int y = startY; y < endY; ++y) {
                patchedWorld.checkLightFor(EnumSkyBlock.SKY, x, y, z);
            }
            this.dirty = true;
        }
    }

    private final BlockPos.MutableBlockPos getBlockLightOpacityBlockPos = new BlockPos.MutableBlockPos();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("deprecation")
    @Overwrite
    private int getBlockLightOpacity(int x, int y, int z)
    {
        IBlockState state = this.getBlockState(x, y, z); //Forge: Can sometimes be called before we are added to the global world list. So use the less accurate one during that. It'll be recalculated later
        return !loaded ? state.getLightOpacity() : state.getLightOpacity(world, getBlockLightOpacityBlockPos.setPos(this.x << 4 | x & 15, y, this.z << 4 | z & 15));
    }

    @Override
    public int getLightFor(@NotNull EnumSkyBlock type, int x, int y, int z) {
        x = x & 15;
        z = z & 15;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];

        if (extendedblockstorage == NULL_BLOCK_STORAGE) {
            return this.canSeeSky(x, y, z) ? type.defaultLightValue : 0;
        } else if (type == EnumSkyBlock.SKY) {
            return !this.world.provider.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(x, y & 15, z);
        } else {
            return type == EnumSkyBlock.BLOCK ? extendedblockstorage.getBlockLight(x, y & 15, z) : type.defaultLightValue;
        }
    }

    @Override
    public boolean canSeeSky(int x, int y, int z) {
        return y >= this.heightMap[(z & 15) << 4 | x & 15];
    }
}
