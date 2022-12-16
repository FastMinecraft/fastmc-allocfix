package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.QuadSort;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder {
    @Shadow
    private int buildStart;
    @Shadow
    private ByteBuffer buffer;
    @Shadow
    private VertexFormat format;
    @Shadow
    private int vertexCount;

    private QuadSort quadSort;
    private ByteBuffer temp;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$RETURN(int initialCapacity, CallbackInfo ci) {
        temp = buffer.duplicate();
        temp.order(ByteOrder.nativeOrder());
    }

    @Inject(method = "grow(I)V", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/render/BufferBuilder;buffer:Ljava/nio/ByteBuffer;", shift = At.Shift.AFTER))
    private void Inject$grow$RETURN(int initialCapacity, CallbackInfo ci) {
        temp = buffer.duplicate();
        temp.order(ByteOrder.nativeOrder());
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
}
