package dev.fastmc.allocfix.main.render.terrain;

import dev.fastmc.allocfix.IPatchedChunkRendererRegion;
import dev.fastmc.allocfix.IPatchedVertexConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer {
    private static final Direction[] DIRECTIONS = Direction.values();

    @Shadow
    protected abstract void renderQuadsSmooth(
        BlockRenderView world,
        BlockState state,
        BlockPos pos,
        MatrixStack matrix,
        VertexConsumer vertexConsumer,
        List<BakedQuad> quads,
        float[] box,
        BitSet flags,
        BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator,
        int overlay
    );

    @Shadow
    @Final
    private BlockColors colorMap;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings({ "DuplicatedCode", "ForLoopReplaceableByForEach" })
    @Overwrite
    public boolean renderSmooth(
        BlockRenderView world,
        BakedModel model,
        BlockState state,
        BlockPos pos,
        MatrixStack buffer,
        VertexConsumer vertexConsumer,
        boolean cull,
        Random random,
        long seed,
        int overlay
    ) {
        boolean rendered = false;
        if (world instanceof IPatchedChunkRendererRegion) {
            IPatchedChunkRendererRegion patched = (IPatchedChunkRendererRegion) world;
            float[] boxDimension = patched.getBoxDimension();
            BitSet bitSet = patched.getBitSet();
            BlockModelRenderer.AmbientOcclusionCalculator aoCalc = patched.getAmbientOcclusionCalculator((BlockModelRenderer) (Object) this);
            for (int i = 0, directionsLength = DIRECTIONS.length; i < directionsLength; i++) {
                Direction direction = DIRECTIONS[i];
                random.setSeed(seed);
                List<BakedQuad> list = model.getQuads(state, direction, random);
                if (list.isEmpty() || cull && !Block.shouldDrawSide(state, world, pos, direction)) continue;
                this.renderQuadsSmooth(
                    world,
                    state,
                    pos,
                    buffer,
                    vertexConsumer,
                    list,
                    boxDimension,
                    bitSet,
                    aoCalc,
                    overlay
                );
                rendered = true;
            }
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, null, random);
            if (!list.isEmpty()) {
                this.renderQuadsSmooth(
                    world,
                    state,
                    pos,
                    buffer,
                    vertexConsumer,
                    list,
                    boxDimension,
                    bitSet,
                    aoCalc,
                    overlay
                );
                rendered = true;
            }
        } else {
            float[] boxDimension = new float[Direction.values().length * 2];
            BitSet bitSet = new BitSet(3);
            BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = ((BlockModelRenderer) (Object) this).new AmbientOcclusionCalculator();
            for (int i = 0, directionsLength = DIRECTIONS.length; i < directionsLength; i++) {
                Direction direction = DIRECTIONS[i];
                random.setSeed(seed);
                List<BakedQuad> list = model.getQuads(state, direction, random);
                if (list.isEmpty() || cull && !Block.shouldDrawSide(state, world, pos, direction)) continue;
                this.renderQuadsSmooth(
                    world,
                    state,
                    pos,
                    buffer,
                    vertexConsumer,
                    list,
                    boxDimension,
                    bitSet,
                    ambientOcclusionCalculator,
                    overlay
                );
                rendered = true;
            }
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, null, random);
            if (!list.isEmpty()) {
                this.renderQuadsSmooth(
                    world,
                    state,
                    pos,
                    buffer,
                    vertexConsumer,
                    list,
                    boxDimension,
                    bitSet,
                    ambientOcclusionCalculator,
                    overlay
                );
                rendered = true;
            }
        }
        return rendered;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private void renderQuad(
        BlockRenderView world,
        BlockState state,
        BlockPos pos,
        VertexConsumer vertexConsumer,
        MatrixStack.Entry matrixEntry,
        BakedQuad quad,
        float brightness0,
        float brightness1,
        float brightness2,
        float brightness3,
        int light0,
        int light1,
        int light2,
        int light3,
        int overlay
    ) {
        float r;
        float g;
        float b;
        if (quad.hasColor()) {
            int i = this.colorMap.getColor(state, world, pos, quad.getColorIndex());
            b = (float) (i >> 16 & 0xFF) / 255.0f;
            g = (float) (i >> 8 & 0xFF) / 255.0f;
            r = (float) (i & 0xFF) / 255.0f;
        } else {
            b = 1.0f;
            g = 1.0f;
            r = 1.0f;
        }
        ((IPatchedVertexConsumer) vertexConsumer).quad(
            matrixEntry,
            quad,
            brightness0,
            brightness1,
            brightness2,
            brightness3,
            b,
            g,
            r,
            light0,
            light1,
            light2,
            light3,
            overlay,
            true
        );
    }
}
