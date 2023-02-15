package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedBufferBuilder;
import dev.fastmc.allocfix.IPatchedBufferBuilderState;
import dev.fastmc.allocfix.PrimitiveSortHelper;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements IPatchedBufferBuilder {
    @Shadow
    private ByteBuffer buffer;
    @Shadow
    private VertexFormat format;
    @Shadow
    private int vertexCount;
    @Shadow
    private VertexFormat.DrawMode drawMode;
    @Shadow
    private boolean hasNoVertexBuffer;
    @Shadow
    private float sortingCameraX;
    @Shadow
    private float sortingCameraY;
    @Shadow
    private float sortingCameraZ;
    @Shadow
    private int elementOffset;
    @Shadow
    private int batchOffset;
    @Shadow
    private int builtBufferCount;

    @Shadow
    protected abstract void grow(int size);

    @Inject(method = "restoreState", at = @At("RETURN"))
    private void Inject$restoreState$RETURN(BufferBuilder.State state, CallbackInfo ci) {
        primitiveCenters = ((IPatchedBufferBuilderState) state).getPrimitiveCenters();
    }

    @Inject(method = "resetBuilding", at = @At("RETURN"))
    private void Inject$resetBuilding$RETURN(CallbackInfo ci) {
        primitiveCenters = null;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public BufferBuilder.State popState() {
        BufferBuilder.State state = new BufferBuilder.State(
            this.drawMode,
            this.vertexCount,
            null,
            this.sortingCameraX,
            this.sortingCameraY,
            this.sortingCameraZ
        );
        //noinspection DataFlowIssue
        ((IPatchedBufferBuilderState) state).setPrimitiveCenters(primitiveCenters);
        return state;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private BufferBuilder.BuiltBuffer build() {
        int indexCount = this.drawMode.getIndexCount(this.vertexCount);
        int vertexSize = this.hasNoVertexBuffer ? 0 : this.vertexCount * this.format.getVertexSizeByte();
        VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(indexCount);
        boolean sequentialIndex;
        int totalSize;

        if (this.primitiveCenters != null) {
            int indexSize = MathHelper.roundUpToMultiple(indexCount * indexType.size, 4);
            this.grow(indexSize);
            this.writeSortedIndices0(indexType);
            this.elementOffset += indexSize;
            totalSize = vertexSize + indexSize;
            sequentialIndex = false;
        } else {
            sequentialIndex = true;
            totalSize = vertexSize;
        }

        int startOffset = batchOffset;
        this.batchOffset += totalSize;
        ++this.builtBufferCount;
        BufferBuilder.DrawArrayParameters drawArrayParameters = new BufferBuilder.DrawArrayParameters(
            this.format,
            this.vertexCount,
            indexCount,
            this.drawMode,
            indexType,
            this.hasNoVertexBuffer,
            sequentialIndex
        );

        return ((BufferBuilder) (Object) this).new BuiltBuffer(startOffset, drawArrayParameters);
    }

    private final PrimitiveSortHelper helper = new PrimitiveSortHelper();

    private void writeSortedIndices0(@NotNull VertexFormat.IndexType type) {
        int primitiveCount = this.primitiveCenters.length / 3;

        helper.ensureCapacity(primitiveCount);
        float[] distanceArray = helper.getDistanceArray();
        int[] sortArray = helper.getSortArray();
        int[] sortSuppArray = helper.getSortSuppArray();

        for (int i = 0; i < primitiveCount; ++i) {
            int index = i * 3;
            float dx = this.primitiveCenters[index] - this.sortingCameraX;
            float dy = this.primitiveCenters[index + 1] - this.sortingCameraY;
            float dz = this.primitiveCenters[index + 2] - this.sortingCameraZ;
            distanceArray[i] = dx * dx + dy * dy + dz * dz;
            sortArray[i] = i;
            sortSuppArray[i] = i;
        }
        IntArrays.mergeSort(sortArray, 0, primitiveCount, helper, sortSuppArray);

        int prevPosition = this.buffer.position();
        this.buffer.position(this.elementOffset);
        int drawModeSize = this.drawMode.additionalVertexCount;

        switch (type) {
            case BYTE -> {
                for (int i = 0; i < primitiveCount; ++i) {
                    int index = sortArray[i] * drawModeSize;
                    buffer.put((byte) index);
                    buffer.put((byte) (index + 1));
                    buffer.put((byte) (index + 2));
                    buffer.put((byte) (index + 2));
                    buffer.put((byte) (index + 3));
                    buffer.put((byte) index);
                }
            }
            case SHORT -> {
                for (int i = 0; i < primitiveCount; ++i) {
                    int index = sortArray[i] * drawModeSize;
                    buffer.putShort((short) index);
                    buffer.putShort((short) (index + 1));
                    buffer.putShort((short) (index + 2));
                    buffer.putShort((short) (index + 2));
                    buffer.putShort((short) (index + 3));
                    buffer.putShort((short) index);
                }
            }
            default -> {
                for (int i = 0; i < primitiveCount; ++i) {
                    int index = sortArray[i] * drawModeSize;
                    buffer.putInt(index);
                    buffer.putInt(index + 1);
                    buffer.putInt(index + 2);
                    buffer.putInt(index + 2);
                    buffer.putInt(index + 3);
                    buffer.putInt(index);
                }
            }
        }

        this.buffer.position(prevPosition);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings({ "DuplicatedCode", "SameReturnValue" })
    @Overwrite
    private Vec3f[] buildPrimitiveCenters() {
        if (primitiveCenters != null) {
            return null;
        }

        int offset = this.batchOffset;
        int vertexSize = this.format.getVertexSizeByte();
        int primitiveSize = vertexSize * this.drawMode.additionalVertexCount;
        int primitiveCount = this.vertexCount / this.drawMode.additionalVertexCount;
        int vertexSize2 = vertexSize * 2;

        primitiveCenters = new float[primitiveCount * 3];

        for (int i = 0; i < primitiveCount; ++i) {
            int primitiveVertexOffset = offset + i * primitiveSize;
            float x1 = buffer.getFloat(primitiveVertexOffset);
            float y1 = buffer.getFloat(primitiveVertexOffset + 4);
            float z1 = buffer.getFloat(primitiveVertexOffset + 8);
            float x2 = buffer.getFloat(primitiveVertexOffset + vertexSize2);
            float y2 = buffer.getFloat(primitiveVertexOffset + vertexSize2 + 4);
            float z2 = buffer.getFloat(primitiveVertexOffset + vertexSize2 + 8);

            int index = i * 3;
            primitiveCenters[index] = (x1 + x2) / 2.0f;
            primitiveCenters[index + 1] = (y1 + y2) / 2.0f;
            primitiveCenters[index + 2] = (z1 + z2) / 2.0f;
        }

        return null;
    }

    private float[] primitiveCenters;

    @Override
    public float @Nullable [] getPrimitiveCenters() {
        return primitiveCenters;
    }

    @Override
    public void setPrimitiveCenters(float[] primitiveCenters) {
        this.primitiveCenters = primitiveCenters;
    }
}
