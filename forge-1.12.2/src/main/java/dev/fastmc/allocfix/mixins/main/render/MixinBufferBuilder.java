package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.mixins.DistanceComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder {
    @Shadow
    private int vertexCount;

    @Shadow
    private static float getDistanceSq(
        FloatBuffer floatBufferIn,
        float x,
        float y,
        float z,
        int integerSize,
        int offset
    ) {
        return 0;
    }

    @Shadow
    private FloatBuffer rawFloatBuffer;

    @Shadow
    private double xOffset;

    @Shadow
    private double yOffset;

    @Shadow
    private double zOffset;

    @Shadow
    private VertexFormat vertexFormat;

    @Shadow
    private IntBuffer rawIntBuffer;

    @Shadow
    protected abstract int getBufferSize();

    private int[] sortArray = new int[0];
    private int[] sortSuppArray = new int[0];
    private final DistanceComparator comparator = new DistanceComparator();

    private final BitSet bitset = new BitSet();
    private int[] buffer = new int[0];

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
        int quadCount = this.vertexCount / 4;

        if (sortArray.length < quadCount) {
            sortArray = new int[quadCount + quadCount >> 1];
            sortSuppArray = new int[sortArray.length];
            comparator.setDistances(new float[sortArray.length]);
        }

        float[] distances = comparator.getDistances();

        for (int j = 0; j < quadCount; ++j) {
            distances[j] = getDistanceSq(
                this.rawFloatBuffer,
                (float) ((double) cameraX + this.xOffset),
                (float) ((double) cameraY + this.yOffset),
                (float) ((double) cameraZ + this.zOffset),
                this.vertexFormat.getIntegerSize(),
                j * this.vertexFormat.getSize()
            );
        }

        for (int i = 0; i < quadCount; ++i) {
            sortArray[i] = i;
            sortSuppArray[i] = i;
        }
        IntArrays.mergeSort(sortArray, 0, quadCount, comparator, sortSuppArray);

        int vertexSize = this.vertexFormat.getSize();
        if (vertexSize > buffer.length) {
            buffer = new int[vertexSize];
        }

        IntBuffer temp = this.rawIntBuffer.duplicate();

        bitset.clear();
        for (int indexFrom = bitset.nextClearBit(0); indexFrom < quadCount; indexFrom = bitset.nextClearBit(indexFrom + 1)) {
            int indexTo = sortArray[indexFrom];

            if (indexTo != indexFrom) {
                this.rawIntBuffer.limit(indexTo * vertexSize + vertexSize);
                this.rawIntBuffer.position(indexTo * vertexSize);
                this.rawIntBuffer.get(buffer, 0, vertexSize);

                int swapToIndex = indexTo;

                for (int swapFromIndex = sortArray[indexTo]; swapToIndex != indexFrom; swapFromIndex = sortArray[swapFromIndex]) {
                    temp.limit(swapFromIndex * vertexSize + vertexSize);
                    temp.position(swapFromIndex * vertexSize);

                    this.rawIntBuffer.limit(swapToIndex * vertexSize + vertexSize);
                    this.rawIntBuffer.position(swapToIndex * vertexSize);
                    this.rawIntBuffer.put(temp);

                    bitset.set(swapToIndex);
                    swapToIndex = swapFromIndex;
                }

                this.rawIntBuffer.limit(indexFrom * vertexSize + vertexSize);
                this.rawIntBuffer.position(indexFrom * vertexSize);
                this.rawIntBuffer.put(buffer, 0, vertexSize);
            }

            bitset.set(indexFrom);
        }

        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(this.getBufferSize());
    }
}
