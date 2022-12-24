package dev.fastmc.allocfix.main.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {
    protected MixinClientWorld(
        MutableWorldProperties properties,
        RegistryKey<World> registryRef,
        RegistryEntry<DimensionType> registryEntry,
        Supplier<Profiler> profiler,
        boolean isClient,
        boolean debugWorld,
        long seed
    ) {
        super(properties, registryRef, registryEntry, profiler, isClient, debugWorld, seed);
    }

    @Shadow
    protected abstract void addParticle(BlockPos pos, BlockState state, ParticleEffect parameters, boolean bl);


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
}
