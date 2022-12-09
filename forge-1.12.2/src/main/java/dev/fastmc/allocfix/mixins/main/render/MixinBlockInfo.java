package dev.fastmc.allocfix.mixins.main.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockInfo.class)
public abstract class MixinBlockInfo {
    @Shadow
    private BlockPos blockPos;

    @Shadow
    private IBlockAccess world;
    @Shadow
    @Final
    private boolean[][][] t;
    @Shadow
    @Final
    private int[][][] s;
    @Shadow
    @Final
    private int[][][] b;
    @Shadow
    @Final
    private float[][][] ao;
    @Shadow
    @Final
    private static EnumFacing[] SIDES;
    @Shadow
    private IBlockState state;

    @Shadow
    protected abstract float combine(
        int c,
        int s1,
        int s2,
        int s3,
        boolean t0,
        boolean t1,
        boolean t2,
        boolean t3
    );

    @Shadow
    @Final
    private float[][][][] skyLight;
    @Shadow
    @Final
    private float[][][][] blockLight;

    private final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void updateLightMatrix() {
        for (int x = 1; x <= 1; x++) {
            for (int y = 1; y <= 1; y++) {
                for (int z = 1; z <= 1; z++) {
                    mutableBlockPos.setPos(x + 1, y + 1, z + 1);
                    IBlockState state = world.getBlockState(mutableBlockPos);
                    t[x][y][z] = state.getLightOpacity(world, mutableBlockPos) < 15;
                    int brightness = state.getPackedLightmapCoords(world, mutableBlockPos);
                    s[x][y][z] = (brightness >> 0x14) & 0xF;
                    b[x][y][z] = (brightness >> 0x04) & 0xF;
                    ao[x][y][z] = state.getAmbientOcclusionLightValue();
                }
            }
        }
        for (EnumFacing side : SIDES) {
            if (!state.doesSideBlockRendering(world, blockPos, side)) {
                int x = side.getXOffset() + 1;
                int y = side.getYOffset() + 1;
                int z = side.getZOffset() + 1;
                s[x][y][z] = Math.max(s[1][1][1] - 1, s[x][y][z]);
                b[x][y][z] = Math.max(b[1][1][1] - 1, b[x][y][z]);
            }
        }
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    int x1 = x * 2;
                    int y1 = y * 2;
                    int z1 = z * 2;

                    int sxyz = s[x1][y1][z1];
                    int bxyz = b[x1][y1][z1];
                    boolean txyz = t[x1][y1][z1];

                    int sxz = s[x1][1][z1], sxy = s[x1][y1][1], syz = s[1][y1][z1];
                    int bxz = b[x1][1][z1], bxy = b[x1][y1][1], byz = b[1][y1][z1];
                    boolean txz = t[x1][1][z1], txy = t[x1][y1][1], tyz = t[1][y1][z1];

                    int sx = s[x1][1][1], sy = s[1][y1][1], sz = s[1][1][z1];
                    int bx = b[x1][1][1], by = b[1][y1][1], bz = b[1][1][z1];
                    boolean tx = t[x1][1][1], ty = t[1][y1][1], tz = t[1][1][z1];

                    skyLight[0][x][y][z] = combine(sx, sxz, sxy, txz || txy ? sxyz : sx,
                        tx, txz, txy, txz || txy ? txyz : tx
                    );
                    blockLight[0][x][y][z] = combine(bx, bxz, bxy, txz || txy ? bxyz : bx,
                        tx, txz, txy, txz || txy ? txyz : tx
                    );

                    skyLight[1][x][y][z] = combine(sy, sxy, syz, txy || tyz ? sxyz : sy,
                        ty, txy, tyz, txy || tyz ? txyz : ty
                    );
                    blockLight[1][x][y][z] = combine(by, bxy, byz, txy || tyz ? bxyz : by,
                        ty, txy, tyz, txy || tyz ? txyz : ty
                    );

                    skyLight[2][x][y][z] = combine(sz, syz, sxz, tyz || txz ? sxyz : sz,
                        tz, tyz, txz, tyz || txz ? txyz : tz
                    );
                    blockLight[2][x][y][z] = combine(bz, byz, bxz, tyz || txz ? bxyz : bz,
                        tz, tyz, txz, tyz || txz ? txyz : tz
                    );
                }
            }
        }
    }
}
