package dev.fastmc.allocfix;

import dev.fastmc.common.MathUtilsKt;
import net.minecraft.util.math.MathHelper;

public class PatchedCubicSampler {
    private static final float[] DENSITY_CURVE = new float[]{ 0.0f, 1.0f, 4.0f, 6.0f, 4.0f, 1.0f, 0.0f };

    public static int sampleColor(double x, double y, double z, RgbFetcher rgbFetcher) {
        int intX = MathUtilsKt.fastFloor(x);
        int intY = MathUtilsKt.fastFloor(y);
        int intZ = MathUtilsKt.fastFloor(z);
        float dx = (float) (x - intX);
        float dy = (float) (y - intY);
        float dz = (float) (z - intZ);

        float total = 0.0f;

        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;

        for (int ix = 0; ix < 6; ++ix) {
            float weightX = MathHelper.lerp(dx, DENSITY_CURVE[ix + 1], DENSITY_CURVE[ix]);
            int sampleX = intX - 2 + ix;

            for (int iy = 0; iy < 6; ++iy) {
                float weightY = MathHelper.lerp(dy, DENSITY_CURVE[iy + 1], DENSITY_CURVE[iy]);
                int sampleY = intY - 2 + iy;

                for (int iz = 0; iz < 6; ++iz) {
                    float weightZ = MathHelper.lerp(dz, DENSITY_CURVE[iz + 1], DENSITY_CURVE[iz]);
                    int sampleZ = intZ - 2 + iz;
                    float weight = weightX * weightY * weightZ;

                    int color = rgbFetcher.fetch(sampleX, sampleY, sampleZ);
                    r += (float) (color >> 20 & 0x3FF) * weight;
                    g += (float) (color >> 10 & 0x3FF) * weight;
                    b += (float) (color & 0x3FF) * weight;
                    total += weight;
                }
            }
        }

        total = 1.0f / total;

        return (int) (r * total) << 20 |
            (int) (g * total) << 10 |
            (int) (b * total);
    }

    public static int rgb8BitsTo10Bits(int color) {
        return (int) ((color >> 16 & 0xFF) * 4.0117645f) << 20 |
           (int) ((color >> 8 & 0xFF) * 4.0117645f) << 10 |
            (int) ((color & 0xFF) * 4.0117645f);
    }

    public static int rgb10BitsTo8Bits(int color) {
        return (color & 0x3FF00000) >> 6 & 0xFF0000 |
            (color & 0xFFC00) >> 4 & 0xFF00 |
            (color & 0x3FF) >> 2;
    }

    @FunctionalInterface
    public interface RgbFetcher {
        int fetch(int x, int y, int z);
    }
}
