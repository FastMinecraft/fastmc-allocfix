package dev.fastmc.allocfix.main.render;

import com.mojang.datafixers.util.Pair;
import dev.fastmc.allocfix.IPatchedBufferBuilder;
import dev.fastmc.allocfix.IPatchedBufferBuilderState;
import dev.fastmc.allocfix.PrimitiveSortHelper;
import dev.fastmc.common.BufferUtils;
import dev.fastmc.common.sort.IntIntrosort;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.util.List;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements IPatchedBufferBuilder {
    @Shadow
    private int buildStart;
    @Shadow
    private ByteBuffer buffer;
    @Shadow
    private VertexFormat format;
    @Shadow
    private int vertexCount;
    @Shadow
    @Final
    private List<BufferBuilder.DrawArrayParameters> parameters;
    @Shadow
    private int lastParameterIndex;
    @Shadow
    private int nextDrawStart;
    @Shadow
    private VertexFormat.DrawMode drawMode;
    @Shadow
    private boolean building;
    @Shadow
    private boolean hasNoVertexBuffer;
    @Shadow
    private @Nullable VertexFormatElement currentElement;
    @Shadow
    private int currentElementId;
    @Shadow
    private float sortingCameraX;
    @Shadow
    private float sortingCameraY;
    @Shadow
    private float sortingCameraZ;
    @Shadow
    private int elementOffset;

    @Shadow
    public abstract void clear();

    @Shadow
    protected abstract void grow(int size);

    private ObjectArrayList<ByteBuffer> cachedBuffers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$RETURN(int initialCapacity, CallbackInfo ci) {
        cachedBuffers = null;
    }

    @Inject(method = "grow(I)V", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/BufferBuilder;buffer:Ljava/nio/ByteBuffer;", shift = At.Shift.AFTER))
    private void Inject$grow$RETURN(int initialCapacity, CallbackInfo ci) {
        cachedBuffers = null;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> popData() {
        BufferBuilder.DrawArrayParameters drawArrayParameters = this.parameters.get(this.lastParameterIndex++);
        this.buffer.position(this.nextDrawStart);
        this.nextDrawStart += MathHelper.roundUpToMultiple(drawArrayParameters.getIndexBufferEnd(), 4);
        this.buffer.limit(this.nextDrawStart);
        if (this.lastParameterIndex == this.parameters.size() && this.vertexCount == 0) {
            this.clear();
        }
        ByteBuffer byteBuffer;
        if (cachedBuffers == null || cachedBuffers.isEmpty()) {
            byteBuffer = this.buffer.slice();
        } else {
            byteBuffer = cachedBuffers.remove(cachedBuffers.size() - 1);
            BufferUtils.setAddress(byteBuffer, BufferUtils.getAddress(this.buffer));
            BufferUtils.setPosition(byteBuffer, BufferUtils.getPosition(this.buffer));
            BufferUtils.setLimit(byteBuffer, BufferUtils.getLimit(this.buffer));
            BufferUtils.setCapacity(byteBuffer, this.buffer.remaining());
        }
        this.buffer.clear();
        return Pair.of(drawArrayParameters, byteBuffer);
    }

    @Override
    public void put(@NotNull ByteBuffer byteBuffer) {
        if (cachedBuffers == null) {
            cachedBuffers = new ObjectArrayList<>();
        }
        cachedBuffers.add(byteBuffer);
    }

    @Inject(method = "restoreState", at = @At("RETURN"))
    private void Inject$restoreState$RETURN(BufferBuilder.State state, CallbackInfo ci) {
        primitiveCenters = ((IPatchedBufferBuilderState) state).getPrimitiveCenters();
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
    public void end() {
        boolean noIndexBuffer;
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
        int i = this.drawMode.getSize(this.vertexCount);
        VertexFormat.IntType intType = VertexFormat.IntType.getSmallestTypeFor(i);
        if (this.primitiveCenters != null) {
            int indexSize = MathHelper.roundUpToMultiple(i * intType.size, 4);
            this.grow(indexSize);
            this.writeSortedIndices0(intType);
            this.elementOffset += indexSize;
            this.buildStart += this.vertexCount * this.format.getVertexSize() + indexSize;
            noIndexBuffer = false;
        } else {
            this.buildStart += this.vertexCount * this.format.getVertexSize();
            noIndexBuffer = true;
        }
        this.building = false;
        this.parameters.add(new BufferBuilder.DrawArrayParameters(
            this.format,
            this.vertexCount,
            i,
            this.drawMode,
            intType,
            this.hasNoVertexBuffer,
            noIndexBuffer
        ));
        this.vertexCount = 0;
        this.currentElement = null;
        this.currentElementId = 0;
        this.primitiveCenters = null;
        this.sortingCameraX = Float.NaN;
        this.sortingCameraY = Float.NaN;
        this.sortingCameraZ = Float.NaN;
        this.hasNoVertexBuffer = false;
    }

    private final PrimitiveSortHelper helper = new PrimitiveSortHelper();

    private void writeSortedIndices0(@NotNull VertexFormat.IntType type) {
        int primitiveCount = this.primitiveCenters.length / 3;

        helper.ensureCapacity(primitiveCount);
        float[] distanceArray = helper.getDistanceArray();
        int[] sortArray = helper.getSortArray();

        for (int i = 0; i < primitiveCount; ++i) {
            int index = i * 3;
            float dx = this.primitiveCenters[index] - this.sortingCameraX;
            float dy = this.primitiveCenters[index + 1] - this.sortingCameraY;
            float dz = this.primitiveCenters[index + 2] - this.sortingCameraZ;
            distanceArray[i] = dx * dx + dy * dy + dz * dz;
            sortArray[i] = i;
        }

        IntIntrosort.sort(sortArray, 0, primitiveCount, helper.getDistanceArray());

        this.buffer.position(this.elementOffset);
        int drawModeSize = this.drawMode.size;

        switch (type) {
            case BYTE -> {
                for (int i = 0; i < primitiveCount; ++i) {
                    int index = sortArray[i] * drawModeSize;
                    buffer.put((byte) (index));
                    buffer.put((byte) (index + 1));
                    buffer.put((byte) (index + 2));
                    buffer.put((byte) (index + 2));
                    buffer.put((byte) (index + 3));
                    buffer.put((byte) (index));
                }
            }
            case SHORT -> {
                for (int i = 0; i < primitiveCount; ++i) {
                    int index = sortArray[i] * drawModeSize;
                    buffer.putShort((short) (index));
                    buffer.putShort((short) (index + 1));
                    buffer.putShort((short) (index + 2));
                    buffer.putShort((short) (index + 2));
                    buffer.putShort((short) (index + 3));
                    buffer.putShort((short) (index));
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

        int offset = this.buildStart;
        int vertexSize = this.format.getVertexSize();
        int primitiveSize = vertexSize * this.drawMode.size;
        int primitiveCount = this.vertexCount / this.drawMode.size;
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
