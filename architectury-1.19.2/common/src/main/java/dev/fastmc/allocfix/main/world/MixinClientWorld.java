package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedClientWorld;
import dev.fastmc.allocfix.PatchedCubicSampler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@SuppressWarnings("DuplicatedCode")
@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World implements IPatchedClientWorld {

    protected MixinClientWorld(
        MutableWorldProperties properties,
        RegistryKey<World> registryRef,
        RegistryEntry<DimensionType> dimension,
        Supplier<Profiler> profiler,
        boolean isClient,
        boolean debugWorld,
        long seed,
        int maxChainedNeighborUpdates
    ) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Shadow
    protected abstract void addParticle(BlockPos pos, BlockState state, ParticleEffect parameters, boolean bl);


    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int lightningTicksLeft;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void randomBlockDisplayTick(
        int centerX,
        int centerY,
        int centerZ,
        int radius,
        Random random,
        @Nullable Block block,
        BlockPos.Mutable pos
    ) {
        int i = centerX + this.random.nextInt(radius) - this.random.nextInt(radius);
        int j = centerY + this.random.nextInt(radius) - this.random.nextInt(radius);
        int k = centerZ + this.random.nextInt(radius) - this.random.nextInt(radius);
        pos.set(i, j, k);
        BlockState blockState = this.getBlockState(pos);
        blockState.getBlock().randomDisplayTick(blockState, this, pos, random);
        FluidState fluidState = this.getFluidState(pos);
        if (!fluidState.isEmpty()) {
            fluidState.randomDisplayTick(this, pos, random);
            ParticleEffect particleEffect = fluidState.getParticle();
            if (particleEffect != null && this.random.nextInt(10) == 0) {
                boolean bl = blockState.isSideSolidFullSquare(this, pos, Direction.DOWN);
                BlockPos blockPos = pos.down();
                this.addParticle(blockPos, this.getBlockState(blockPos), particleEffect, bl);
            }
        }

        if (block == blockState.getBlock()) {
            this.addParticle(
                new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, blockState),
                (double) i + 0.5,
                (double) j + 0.5,
                (double) k + 0.5,
                0.0,
                0.0,
                0.0
            );
        }

        if (!blockState.isFullCube(this, pos)) {
            BiomeParticleConfig config = this.getBiome(pos).value().getParticleConfig().orElse(null);
            if (config != null && config.shouldAddParticle(this.random)) {
                this.addParticle(
                    config.getParticle(),
                    (double) pos.getX() + this.random.nextDouble(),
                    (double) pos.getY() + this.random.nextDouble(),
                    (double) pos.getZ() + this.random.nextDouble(),
                    0.0,
                    0.0,
                    0.0
                );
            }
        }
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public int calculateColor(BlockPos pos, ColorResolver colorResolver) {
        int i = MinecraftClient.getInstance().options.getBiomeBlendRadius().getValue();
        if (i == 0) {
            return colorResolver.getColor(this.getBiome(pos).value(), pos.getX(), pos.getZ());
        }
        int total = (i * 2 + 1) * (i * 2 + 1);
        int r = 0;
        int g = 0;
        int b = 0;

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int startX = pos.getX() - i;
        int startZ = pos.getZ() - i;
        int endX = pos.getX() + i;
        int endZ = pos.getZ() + i;
        int y = pos.getY();

        for (int x = startX; x <= endX; ++x) {
            for (int z = startZ; z <= endZ; ++z) {
                mutable.set(x, y, z);
                int color = colorResolver.getColor(this.getBiome(mutable).value(), x, z);
                r += color >> 16 & 0xFF;
                g += color >> 8 & 0xFF;
                b += color & 0xFF;
            }
        }


        return (r / total & 0xFF) << 16 | (g / total & 0xFF) << 8 | b / total & 0xFF;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public Vec3d getSkyColor(Vec3d cameraPos, float tickDelta) {
        double posX = cameraPos.x * 0.25 - 0.5;
        double posY = cameraPos.y * 0.25 - 0.5;
        double posZ = cameraPos.z * 0.25 - 0.5;

        float mul = MathHelper.cos(this.getSkyAngle(tickDelta) * ((float) Math.PI * 2)) * 2.0f + 0.5f;
        mul = MathHelper.clamp(mul, 0.0f, 1.0f) / 1023.0f;

        BiomeAccess biomeAccess = this.getBiomeAccess();

        int rawColor = PatchedCubicSampler.sampleColor(
            posX,
            posY,
            posZ,
            (x, y, z) -> PatchedCubicSampler.rgb8BitsTo10Bits(
                biomeAccess.getBiomeForNoiseGen(x, y, z).value().getSkyColor()
            )
        );
        float r = (float) (rawColor >> 20 & 0x3FF) * mul;
        float g = (float) (rawColor >> 10 & 0x3FF) * mul;
        float b = (float) (rawColor & 0x3FF) * mul;

        float k = this.getRainGradient(tickDelta);

        if (k > 0.0f) {
            float m;
            float l;
            l = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.6f;
            m = 1.0f - k * 0.75f;
            r = r * m + l * (1.0f - m);
            g = g * m + l * (1.0f - m);
            b = b * m + l * (1.0f - m);
        }

        float thunderGradient = this.getThunderGradient(tickDelta);
        if (thunderGradient > 0.0f) {
            float m = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.2f;
            float n = 1.0f - thunderGradient * 0.75f;
            r = r * n + m * (1.0f - n);
            g = g * n + m * (1.0f - n);
            b = b * n + m * (1.0f - n);
        }

        if (!this.client.options.getHideLightningFlashes().getValue() && this.lightningTicksLeft > 0) {
            float m = Math.min((float) this.lightningTicksLeft - tickDelta, 1.0f) * 0.45f;
            r = r * (1.0f - m) + 0.8f * m;
            g = g * (1.0f - m) + 0.8f * m;
            b = b * (1.0f - m) + m;
        }

        return new Vec3d(r, g, b);
    }

    @Override
    public int getSkyColor10Bit(Vec3d cameraPos, float tickDelta) {
        double posX = cameraPos.x * 0.25 - 0.5;
        double posY = cameraPos.y * 0.25 - 0.5;
        double posZ = cameraPos.z * 0.25 - 0.5;

        float mul = MathHelper.cos(this.getSkyAngle(tickDelta) * ((float) Math.PI * 2)) * 2.0f + 0.5f;
        mul = MathHelper.clamp(mul, 0.0f, 1.0f);

        BiomeAccess biomeAccess = this.getBiomeAccess();

        int rawColor = PatchedCubicSampler.sampleColor(
            posX,
            posY,
            posZ,
            (x, y, z) -> PatchedCubicSampler.rgb8BitsTo10Bits(
                biomeAccess.getBiomeForNoiseGen(x, y, z).value().getSkyColor()
            )
        );
        float r = (float) (rawColor >> 20 & 0x3FF) * mul;
        float g = (float) (rawColor >> 10 & 0x3FF) * mul;
        float b = (float) (rawColor & 0x3FF) * mul;

        float k = this.getRainGradient(tickDelta);

        if (k > 0.0f) {
            float m;
            float l;
            l = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.6f;
            m = 1.0f - k * 0.75f;
            r = r * m + l * (1.0f - m);
            g = g * m + l * (1.0f - m);
            b = b * m + l * (1.0f - m);
        }

        float thunderGradient = this.getThunderGradient(tickDelta);
        if (thunderGradient > 0.0f) {
            float m = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.2f;
            float n = 1.0f - thunderGradient * 0.75f;
            r = r * n + m * (1.0f - n);
            g = g * n + m * (1.0f - n);
            b = b * n + m * (1.0f - n);
        }

        if (!this.client.options.getHideLightningFlashes().getValue() && this.lightningTicksLeft > 0) {
            float m = Math.min((float) this.lightningTicksLeft - tickDelta, 1.0f) * 0.45f;
            r = r * (1.0f - m) + 0.8f * m;
            g = g * (1.0f - m) + 0.8f * m;
            b = b * (1.0f - m) + m;
        }

        return ((int) r & 0x3FF) << 20 |
            ((int) g & 0x3FF) << 10 |
            ((int) b & 0x3FF);
    }
}
