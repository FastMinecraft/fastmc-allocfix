package dev.fastmc.allocfix.main;

import dev.fastmc.allocfix.accessor.AccessorVoxelShape;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VoxelShapes.class)
public abstract class MixinVoxelShapes {

    @Shadow
    @Final
    private static VoxelShape FULL_CUBE;

    @Shadow
    private static int findRequiredBitResolution(double min, double max) {
        return 0;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static VoxelShape cuboid(Box box) {
        return cuboid(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static VoxelShape cuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        int i = findRequiredBitResolution(minX, maxX);
        int j = findRequiredBitResolution(minY, maxY);
        int k = findRequiredBitResolution(minZ, maxZ);
        if (i < 0 || j < 0 || k < 0) {
            return new ArrayVoxelShape(
                ((AccessorVoxelShape) FULL_CUBE).getVoxels(),
                DoubleArrayList.wrap(new double[]{ minX, maxX }),
                DoubleArrayList.wrap(new double[]{ minY, maxY }),
                DoubleArrayList.wrap(new double[]{ minZ, maxZ })
            );
        }
        if (i == 0 && j == 0 && k == 0) {
            return 0.5 >= minX && 0.5 < maxX && 0.5 >= minY && 0.5 < maxY && 0.5 >= minZ && 0.5 < maxZ ? VoxelShapes.fullCube() : VoxelShapes.empty();
        }

        int l = 1 << i;
        int m = 1 << j;
        int n = 1 << k;

        int o = (int) Math.round(minX * (double) l);
        int p = (int) Math.round(maxX * (double) l);
        int q = (int) Math.round(minY * (double) m);

        int r = (int) Math.round(maxY * (double) m);
        int s = (int) Math.round(minZ * (double) n);
        int t = (int) Math.round(maxZ * (double) n);

        BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(l, m, n, o, q, s, p, r, t);
        for (long u = o; u < (long) p; ++u) {
            for (long v = q; v < (long) r; ++v) {
                for (long w = s; w < (long) t; ++w) {
                    bitSetVoxelSet.set((int) u, (int) v, (int) w, false, true);
                }
            }
        }
        return new SimpleVoxelShape(bitSetVoxelSet);
    }
}
