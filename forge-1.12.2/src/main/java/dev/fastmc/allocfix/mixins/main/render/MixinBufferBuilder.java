package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.QuadSort;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
    private QuadSort quadSort;
    private ByteBuffer temp;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$RETURN(int initialCapacity, CallbackInfo ci) {
        temp = byteBuffer.duplicate();
        temp.order(ByteOrder.nativeOrder());
    }


    @Inject(method = "growBuffer", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/renderer/BufferBuilder;byteBuffer:Ljava/nio/ByteBuffer;", shift = At.Shift.AFTER))
    private void Inject$grow$FIELD$PUTFIELD$byteBuffer(int initialCapacity, CallbackInfo ci) {
        temp = byteBuffer.duplicate();
        temp.order(ByteOrder.nativeOrder());
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
        if (quadSort == null) {
            quadSort = new QuadSort();
        }

        int prevPos = byteBuffer.position();
        int prevLimit = byteBuffer.limit();

        VertexFormat format = vertexFormat;
        quadSort.sortQuads(
            byteBuffer,
            temp,
            0,
            (float) (cameraX + xOffset),
            (float) (cameraY + yOffset),
            (float) (cameraZ + zOffset),
            vertexCount,
            format.getSize()
        );

        byteBuffer.position(prevPos);
        byteBuffer.limit(prevLimit);
    }
}
