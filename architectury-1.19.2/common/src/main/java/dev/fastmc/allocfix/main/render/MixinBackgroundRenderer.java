package dev.fastmc.allocfix.main.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.fastmc.allocfix.IPatchedClientWorld;
import dev.fastmc.allocfix.IPatchedDimensionEffects;
import dev.fastmc.allocfix.PatchedCubicSampler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    @Shadow
    private static long lastWaterFogColorUpdateTime;

    @Shadow
    private static int waterFogColor;

    @Shadow
    private static int nextWaterFogColor;

    @Shadow
    private static float red;

    @Shadow
    private static float green;

    @Shadow
    private static float blue;

    private static final BlockPos.Mutable mutable = new BlockPos.Mutable();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static void render(Camera camera, float tickDelta, ClientWorld world, int i2, float f) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        Vec3d cameraPos = camera.getPos();
        IPatchedClientWorld patchedWorld = (IPatchedClientWorld) world;

        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            long currentTime = Util.getMeasuringTimeMs();
            mutable.set(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
            int currentColor = world.getBiome(mutable).value().getWaterFogColor();
            if (lastWaterFogColorUpdateTime < 0L) {
                waterFogColor = currentColor;
                nextWaterFogColor = currentColor;
                lastWaterFogColorUpdateTime = currentTime;
            }
            int cr = waterFogColor >> 16 & 0xFF;
            int cg = waterFogColor >> 8 & 0xFF;
            int cb = waterFogColor & 0xFF;
            int nr = nextWaterFogColor >> 16 & 0xFF;
            int ng = nextWaterFogColor >> 8 & 0xFF;
            int nb = nextWaterFogColor & 0xFF;
            float time = MathHelper.clamp((float) (currentTime - lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float r = MathHelper.lerp(time, nr, cr);
            float g = MathHelper.lerp(time, ng, cg);
            float b = MathHelper.lerp(time, nb, cb);

            red = r / 255.0f;
            green = g / 255.0f;
            blue = b / 255.0f;
            if (waterFogColor != currentColor) {
                waterFogColor = currentColor;
                nextWaterFogColor = MathHelper.floor(r) << 16 | MathHelper.floor(g) << 8 | MathHelper.floor(b);
                lastWaterFogColorUpdateTime = currentTime;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
            lastWaterFogColorUpdateTime = -1L;
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            red = 0.623f;
            green = 0.734f;
            blue = 0.785f;
            lastWaterFogColorUpdateTime = -1L;
            RenderSystem.clearColor(red, green, blue, 0.0f);
        } else {
            float t = 0.25f + 0.75f * i2 / 32.0f;
            t = 1.0f - (float) Math.pow(t, 0.25);

            float skyAngle = world.getSkyAngle(tickDelta);
            float sunHeight = MathHelper.clamp(
                MathHelper.cos(skyAngle * ((float) Math.PI * 2)) * 2.0f + 0.5f,
                0.0f,
                1.0f
            );
            BiomeAccess biomeAccess = world.getBiomeAccess();
            Vec3d vec3d2 = cameraPos.subtract(2.0, 2.0, 2.0).multiply(0.25);

            IPatchedDimensionEffects patchedDimensionEffects = (IPatchedDimensionEffects) world.getDimensionEffects();
            int fogColor = PatchedCubicSampler.sampleColor(
                vec3d2.x,
                vec3d2.y,
                vec3d2.z,
                (x, y, z) -> patchedDimensionEffects.adjustFogColor(
                    PatchedCubicSampler.rgb8BitsTo10Bits(
                        biomeAccess.getBiomeForNoiseGen(x, y, z).value().getFogColor()
                    ),
                    sunHeight
                )
            );
            red = (fogColor >> 20 & 0x3FF) / 1023.0f;
            green = (fogColor >> 10 & 0x3FF) / 1023.0f;
            blue = (fogColor & 0x3FF) / 1023.0f;

            if (i2 >= 4) {
                float g = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0f ? -1.0f : 1.0f;
                Vec3f vec3f = new Vec3f(g, 0.0f, 0.0f);
                float r = Math.max(camera.getHorizontalPlane().dot(vec3f), 0.0f);

                if (r > 0.0f) {
                    float[] fogColorOverride = world.getDimensionEffects().getFogColorOverride(skyAngle, tickDelta);
                    if (fogColorOverride != null) {
                        red = red * (1.0f - (r *= fogColorOverride[3])) + fogColorOverride[0] * r;
                        green = green * (1.0f - r) + fogColorOverride[1] * r;
                        blue = blue * (1.0f - r) + fogColorOverride[2] * r;
                    }
                }
            }

            int rawSkyColor = patchedWorld.getSkyColor10Bit(cameraPos, tickDelta);
            float rawR = (float) (rawSkyColor >> 20 & 0x3FF) / 1023.0f;
            float rawG = (float) (rawSkyColor >> 10 & 0x3FF) / 1023.0f;
            float rawB = (float) (rawSkyColor & 0x3FF) / 1023.0f;
            red += (rawR - red) * t;
            green += (rawG - green) * t;
            blue += (rawB - blue) * t;
            float rainGradient = world.getRainGradient(tickDelta);
            if (rainGradient > 0.0f) {
                float h2 = 1.0f - rainGradient * 0.5f;
                final float r;
                r = 1.0f - rainGradient * 0.4f;
                red *= h2;
                green *= h2;
                blue *= r;
            }

            float thunderGradient = world.getThunderGradient(tickDelta);
            if (thunderGradient > 0.0f) {
                final float r;
                r = 1.0f - thunderGradient * 0.5f;
                red *= r;
                green *= r;
                blue *= r;
            }

            lastWaterFogColorUpdateTime = -1L;
        }

        float t = ((float) cameraPos.y - (float) world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity) {
            StatusEffectInstance effect = ((LivingEntity) camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS);
            if (effect != null) {
                int y = effect.getDuration();
                t = y < 20 ? 1.0f - (float) y / 20.0f : 0.0f;
            }
        }

        if (t < 1.0f && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
            if (t < 0.0f) {
                t = 0.0f;
            }
            t *= t;
            red *= t;
            green *= t;
            blue *= t;
        }
        if (f > 0.0f) {
            red = red * (1.0f - f) + red * 0.7f * f;
            green = green * (1.0f - f) + green * 0.6f * f;
            blue = blue * (1.0f - f) + blue * 0.6f * f;
        }
        float z = cameraSubmersionType == CameraSubmersionType.WATER ? (entity instanceof ClientPlayerEntity ? ((ClientPlayerEntity) entity).getUnderwaterVisibility() : 1.0f) : (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(
            StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength((LivingEntity) entity, tickDelta) : 0.0f);

        if (red != 0.0f && green != 0.0f && blue != 0.0f) {
            float u = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - z) + red * u * z;
            green = green * (1.0f - z) + green * u * z;
            blue = blue * (1.0f - z) + blue * u * z;
        }

        RenderSystem.clearColor(red, green, blue, 0.0f);
    }
}
