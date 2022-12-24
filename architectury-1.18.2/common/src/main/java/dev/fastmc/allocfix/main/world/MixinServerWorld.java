package dev.fastmc.allocfix.main.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {
    @Shadow
    public abstract PointOfInterestStorage getPointOfInterestStorage();

    @Shadow
    public abstract ServerWorld toServerWorld();

    protected MixinServerWorld(
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

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("DataFlowIssue")
    @Overwrite
    public void tickChunk(WorldChunk chunk, int randomTickSpeed) {
        BlockPos.Mutable mutablePos1 = new BlockPos.Mutable();
        BlockPos.Mutable mutablePos2 = new BlockPos.Mutable();

        ChunkPos chunkPos = chunk.getPos();
        boolean isRaining = this.isRaining();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();

        Profiler profiler = this.getProfiler();
        profiler.push("thunder");
        if (isRaining && this.isThundering() && this.random.nextInt(100000) == 0) {
            getRandomPosInChunk(mutablePos1, startX, 0, startZ);
            getLightningPos(mutablePos1);

            if (this.hasRain(mutablePos1)) {
                LocalDifficulty localDifficulty = this.getLocalDifficulty(mutablePos1);
                boolean flag = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)
                    && this.random.nextDouble() < localDifficulty.getLocalDifficulty() * 0.01
                    && !this.getBlockState(mutablePos2.set(mutablePos1, 0, -1, 0)).isOf(Blocks.LIGHTNING_ROD);

                if (flag) {
                    SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.create(this);
                    skeletonHorseEntity.setTrapped(true);
                    skeletonHorseEntity.setBreedingAge(0);
                    skeletonHorseEntity.setPosition(mutablePos1.getX(), mutablePos1.getY(), mutablePos1.getZ());
                    this.spawnEntity(skeletonHorseEntity);
                }

                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this);
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(mutablePos1));
                lightningEntity.setCosmetic(flag);
                this.spawnEntity(lightningEntity);
            }
        }

        profiler.swap("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            getRandomPosInChunk(mutablePos1, startX, 0, startZ);
            mutablePos1.setY(this.getTopY(Heightmap.Type.MOTION_BLOCKING, mutablePos1.getX(), mutablePos1.getZ()));
            mutablePos2.set(mutablePos1, 0, -1, 0);

            Biome biome = this.getBiome(mutablePos1).value();
            if (biome.canSetIce(this, mutablePos2)) {
                this.setBlockState(mutablePos2, Blocks.ICE.getDefaultState());
            }
            if (isRaining) {
                if (biome.canSetSnow(this, mutablePos1)) {
                    this.setBlockState(mutablePos1, Blocks.SNOW.getDefaultState());
                }
                BlockState blockState = this.getBlockState(mutablePos2);
                Biome.Precipitation precipitation = biome.getPrecipitation();
                if (precipitation == Biome.Precipitation.RAIN && biome.isCold(mutablePos2)) {
                    precipitation = Biome.Precipitation.SNOW;
                }
                blockState.getBlock().precipitationTick(blockState, this, mutablePos2, precipitation);
            }
        }

        profiler.swap("tickBlocks");
        if (randomTickSpeed > 0) {
            for (ChunkSection chunkSection : chunk.getSectionArray()) {
                if (!chunkSection.hasRandomTicks()) continue;
                int k = chunkSection.getYOffset();

                for (int l = 0; l < randomTickSpeed; ++l) {
                    FluidState fluidState;
                    getRandomPosInChunk(mutablePos1, startX, k, startZ);
                    profiler.push("randomTick");

                    BlockState state = chunkSection.getBlockState(
                        mutablePos1.getX() - startX,
                        mutablePos1.getY() - k,
                        mutablePos1.getZ() - startZ
                    );
                    if (state.hasRandomTicks()) {
                        state.randomTick((ServerWorld) (Object) this, mutablePos1, this.random);
                    }
                    if ((fluidState = state.getFluidState()).hasRandomTicks()) {
                        fluidState.onRandomTick(this, mutablePos1, this.random);
                    }

                    profiler.pop();
                }
            }
        }
        profiler.pop();
    }

    private void getRandomPosInChunk(BlockPos.Mutable pos, int x, int y, int z) {
        this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
        int j = this.lcgBlockSeed >> 2;
        pos.set(x + (j & 0xF), y + (j >> 16 & 15), z + (j >> 8 & 0xF));
    }

    private void getLightningPos(BlockPos.Mutable mutablePos) {
        mutablePos.setY(this.getTopY(Heightmap.Type.MOTION_BLOCKING, mutablePos.getX(), mutablePos.getZ()));

        Optional<BlockPos> optional = this.getPointOfInterestStorage().getNearestPosition(
            type -> type == PointOfInterestType.LIGHTNING_ROD,
            it1 -> it1.getY() == this.toServerWorld().getTopY(Heightmap.Type.WORLD_SURFACE, it1.getX(), it1.getZ()) - 1,
            mutablePos,
            128,
            PointOfInterestStorage.OccupationStatus.ANY
        );

        if (optional.isPresent()) {
            mutablePos.set(optional.get(), 0, 1, 0);
            return;
        }

        Box box = new Box(mutablePos, new BlockPos(mutablePos.getX(), this.getTopY(), mutablePos.getZ())).expand(3.0);
        List<LivingEntity> list = this.getEntitiesByClass(
            LivingEntity.class,
            box,
            entity -> entity != null && entity.isAlive() && this.isSkyVisible(entity.getBlockPos())
        );

        if (!list.isEmpty()) {
            mutablePos.set(list.get(this.random.nextInt(list.size())).getBlockPos());
            return;
        }

        if (mutablePos.getY() == this.getBottomY() - 1) {
            mutablePos.add(0, 2, 0);
        }
    }

}
