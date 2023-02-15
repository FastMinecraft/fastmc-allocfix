package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedRenderedChunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(RenderedChunk.class)
public class MixinRenderedChunk implements IPatchedRenderedChunk {
    @Shadow
    @Final
    private boolean debugWorld;

    @Shadow @Final @Nullable private List<PalettedContainer<BlockState>> blockStateContainers;

    @Shadow @Final private WorldChunk chunk;

    @SuppressWarnings("DuplicatedCode")
    public @NotNull BlockState getBlockState(int x, int y, int z) {
        if (this.debugWorld) {
            BlockState blockState = null;
            if (y == 60) {
                blockState = Blocks.BARRIER.getDefaultState();
            }
            if (y == 70) {
                blockState = DebugChunkGenerator.getBlockState(x, z);
            }
            return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
        }
        if (this.blockStateContainers == null) {
            return Blocks.AIR.getDefaultState();
        }
        try {
            PalettedContainer<BlockState> palettedContainer;
            int l = this.chunk.getSectionIndex(y);
            if (l >= 0 && l < this.blockStateContainers.size() && (palettedContainer = this.blockStateContainers.get(l)) != null) {
                return palettedContainer.get(x & 0xF, y & 0xF, z & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add(
                "Location",
                () -> CrashReportSection.createPositionString(this.chunk, x, y, z)
            );
            throw new CrashException(crashReport);
        }
    }
}
