package dev.fastmc.allocfix.main.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dev.fastmc.allocfix.IPatchedBufferBuilder;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;

@Mixin(BufferRenderer.class)
public abstract class MixinBufferRenderer {
    @Shadow
    private static void draw(
        ByteBuffer buffer,
        VertexFormat.DrawMode drawMode,
        VertexFormat vertexFormat,
        int count,
        VertexFormat.IntType elementFormat,
        int vertexCount,
        boolean textured
    ) {
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static void draw(BufferBuilder bufferBuilder) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
                BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
                draw(pair.getSecond(), drawArrayParameters.getMode(), drawArrayParameters.getVertexFormat(), drawArrayParameters.getCount(), drawArrayParameters.getElementFormat(), drawArrayParameters.getVertexCount(), drawArrayParameters.hasNoIndexBuffer());
                ((IPatchedBufferBuilder) bufferBuilder).put(pair.getSecond());
            });
        } else {
            Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
            BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
            draw(pair.getSecond(), drawArrayParameters.getMode(), drawArrayParameters.getVertexFormat(), drawArrayParameters.getCount(), drawArrayParameters.getElementFormat(), drawArrayParameters.getVertexCount(), drawArrayParameters.hasNoIndexBuffer());
            ((IPatchedBufferBuilder) bufferBuilder).put(pair.getSecond());
        }
    }
}
