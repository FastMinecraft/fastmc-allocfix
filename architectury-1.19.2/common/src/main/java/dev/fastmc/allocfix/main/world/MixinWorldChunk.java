package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldChunk.class)
public abstract class MixinWorldChunk extends Chunk implements IPatchedBlockView {
    public MixinWorldChunk(
        ChunkPos pos,
        UpgradeData upgradeData,
        HeightLimitView heightLimitView,
        Registry<Biome> biome,
        long inhabitedTime,
        @Nullable ChunkSection[] sectionArrayInitializer,
        @Nullable BlendingData blendingData
    ) {
        super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
    }

    @Shadow
    @Final
    World world;

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    @Override
    public BlockState getBlockState(int x, int y, int z) {
        if (this.world.isDebugWorld()) {
            BlockState blockState = null;
            if (y == 60) {
                blockState = Blocks.BARRIER.getDefaultState();
            }
            if (y == 70) {
                blockState = DebugChunkGenerator.getBlockState(x, z);
            }
            return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
        }
        try {
            ChunkSection chunkSection;
            int l = this.getSectionIndex(y);
            if (l >= 0 && l < this.sectionArray.length && !(chunkSection = this.sectionArray[l]).isEmpty()) {
                return chunkSection.getBlockState(x & 0xF, y & 0xF, z & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add(
                "Location",
                () -> CrashReportSection.createPositionString(this, x, y, z)
            );
            throw new CrashException(crashReport);
        }
    }
}
