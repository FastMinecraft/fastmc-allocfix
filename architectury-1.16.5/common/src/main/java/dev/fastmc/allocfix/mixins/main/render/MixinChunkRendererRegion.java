package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.mixins.IPatchedChunkRendererRegion;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.BitSet;

@Mixin(ChunkRendererRegion.class)
public class MixinChunkRendererRegion implements IPatchedChunkRendererRegion {
    private BlockModelRenderer.AmbientOcclusionCalculator aoCalculator;
    private final BitSet bitSet = new BitSet(3);
    private final float[] boxDimension = new float[12];
    private final float[] brightness = new float[4];
    private final int[] lights = new int[4];

    @NotNull
    @Override
    public BlockModelRenderer.AmbientOcclusionCalculator getAmbientOcclusionCalculator(@NotNull BlockModelRenderer renderer) {
        if (aoCalculator == null) {
            aoCalculator = renderer.new AmbientOcclusionCalculator();
        }
        return aoCalculator;
    }

    @NotNull
    @Override
    public BitSet getBitSet() {
        return bitSet;
    }

    @Override
    public float @NotNull [] getBoxDimension() {
        return boxDimension;
    }

    @Override
    public float @NotNull [] getBrightness() {
        return brightness;
    }

    @Override
    public int @NotNull [] getLights() {
        return lights;
    }
}
