package dev.fastmc.allocfix.main.render;

import com.mojang.datafixers.util.Pair;
import dev.fastmc.allocfix.IPatchedBufferBuilder;
import dev.fastmc.allocfix.QuadSort;
import dev.fastmc.common.BufferUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    public abstract void clear();

    private ObjectArrayList<ByteBuffer> cachedBuffers;

    private QuadSort quadSort;
    private ByteBuffer temp;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$RETURN(int initialCapacity, CallbackInfo ci) {
        temp = buffer.duplicate();
        temp.order(ByteOrder.nativeOrder());
        cachedBuffers = null;
    }

    @Inject(method = "grow(I)V", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/BufferBuilder;buffer:Ljava/nio/ByteBuffer;", shift = At.Shift.AFTER))
    private void Inject$grow$RETURN(int initialCapacity, CallbackInfo ci) {
        temp = buffer.duplicate();
        temp.order(ByteOrder.nativeOrder());
        cachedBuffers = null;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void sortQuads(float cameraX, float cameraY, float cameraZ) {
        if (quadSort == null) {
            quadSort = new QuadSort();
        }

        int prevPos = buffer.position();
        int prevLimit = buffer.limit();

        VertexFormat format = this.format;
        quadSort.sortQuads(
            buffer,
            temp,
            buildStart,
            cameraX,
            cameraY,
            cameraZ,
            vertexCount,
            format.getVertexSize()
        );

        buffer.position(prevPos);
        buffer.limit(prevLimit);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> popData() {
        BufferBuilder.DrawArrayParameters drawArrayParameters = this.parameters.get(this.lastParameterIndex++);
        this.buffer.position(this.nextDrawStart);
        this.nextDrawStart += drawArrayParameters.getCount() * drawArrayParameters.getVertexFormat().getVertexSize();
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
}
