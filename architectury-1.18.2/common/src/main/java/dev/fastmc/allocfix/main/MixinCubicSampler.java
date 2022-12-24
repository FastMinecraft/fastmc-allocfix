package dev.fastmc.allocfix.main;

import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CubicSampler.class)
public class MixinCubicSampler {
    @Shadow
    @Final
    private static double[] DENSITY_CURVE;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static Vec3d sampleColor(Vec3d pos, CubicSampler.RgbFetcher rgbFetcher) {
        int x = MathHelper.floor(pos.getX());
        int y = MathHelper.floor(pos.getY());
        int z = MathHelper.floor(pos.getZ());
        double dx = pos.getX() - (double) x;
        double dy = pos.getY() - (double) y;
        double dz = pos.getZ() - (double) z;
        double total = 0.0;

        double r = 0.0;
        double g = 0.0;
        double b = 0.0;

        for (int ix = 0; ix < 6; ++ix) {
            double weightX = MathHelper.lerp(dx, DENSITY_CURVE[ix + 1], DENSITY_CURVE[ix]);
            int sampleX = x - 2 + ix;

            for (int iy = 0; iy < 6; ++iy) {
                double weightY = MathHelper.lerp(dy, DENSITY_CURVE[iy + 1], DENSITY_CURVE[iy]);
                int sampleY = y - 2 + iy;

                for (int iz = 0; iz < 6; ++iz) {
                    double weightZ = MathHelper.lerp(dz, DENSITY_CURVE[iz + 1], DENSITY_CURVE[iz]);
                    int sampleZ = z - 2 + iz;
                    double weight = weightX * weightY * weightZ;

                    Vec3d color = rgbFetcher.fetch(sampleX, sampleY, sampleZ);
                    r += color.x * weight;
                    g += color.y * weight;
                    b += color.z * weight;
                    total += weight;
                }
            }
        }

        total = 1.0 / total;
        return new Vec3d(r * total, g * total, b * total);
    }
}
