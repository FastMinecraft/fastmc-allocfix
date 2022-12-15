package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.IPatchedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldChunk.class)
public class MixinWorldChunk implements IPatchedBlockView {
    @Shadow
    @Final
    private World world;

    @Shadow
    @Final
    private ChunkSection[] sections;

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
            if (y >= 0 && y >> 4 < this.sections.length && !ChunkSection.isEmpty(chunkSection = this.sections[y >> 4])) {
                return chunkSection.getBlockState(x & 0xF, y & 0xF, z & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString(x, y, z));
            throw new CrashException(crashReport);
        }
    }
}
