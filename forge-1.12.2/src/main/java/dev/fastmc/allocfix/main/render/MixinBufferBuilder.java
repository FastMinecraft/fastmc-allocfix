package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.QuadSort;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder {
    @Shadow
    private ByteBuffer byteBuffer;
    @Shadow
    private VertexFormat vertexFormat;
    @Shadow
    private double xOffset;
    @Shadow
    private double yOffset;
    @Shadow
    private double zOffset;
    @Shadow
    private int vertexCount;

    @Shadow
    private IntBuffer rawIntBuffer;

    @Shadow
    protected abstract int getBufferSize();

    @Unique
    private QuadSort fastmc_allocfix$quadSort;
    @Unique
    private ByteBuffer fastmc_allocfix$temp;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$RETURN(int initialCapacity, CallbackInfo ci) {
        fastmc_allocfix$temp = byteBuffer.duplicate();
        fastmc_allocfix$temp.order(ByteOrder.nativeOrder());
    }


    @Inject(method = "growBuffer", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/renderer/BufferBuilder;byteBuffer:Ljava/nio/ByteBuffer;", shift = At.Shift.AFTER))
    private void Inject$grow$FIELD$PUTFIELD$byteBuffer(int initialCapacity, CallbackInfo ci) {
        fastmc_allocfix$temp = byteBuffer.duplicate();
        fastmc_allocfix$temp.order(ByteOrder.nativeOrder());
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
        if (fastmc_allocfix$quadSort == null) {
            fastmc_allocfix$quadSort = new QuadSort();
        }

        VertexFormat format = vertexFormat;
        fastmc_allocfix$quadSort.sortQuads(
            byteBuffer,
            fastmc_allocfix$temp,
            0,
            (float) (cameraX + xOffset),
            (float) (cameraY + yOffset),
            (float) (cameraZ + zOffset),
            vertexCount,
            format.getSize()
        );

        byteBuffer.clear();

        rawIntBuffer.limit(rawIntBuffer.capacity());
        rawIntBuffer.position(getBufferSize());
    }
}
