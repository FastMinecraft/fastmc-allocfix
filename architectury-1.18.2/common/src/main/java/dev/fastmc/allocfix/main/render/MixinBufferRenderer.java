package dev.fastmc.allocfix.main.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dev.fastmc.allocfix.IPatchedBufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15C.*;

@Mixin(BufferRenderer.class)
public abstract class MixinBufferRenderer {
    @Shadow
    private static void bind(VertexFormat vertexFormat) {
    }

    @Shadow
    private static int currentElementBuffer;

    private static final String[] samplerNames = new String[] {
        "Sampler0",
        "Sampler1",
        "Sampler2",
        "Sampler3",
        "Sampler4",
        "Sampler5",
        "Sampler6",
        "Sampler7"
    };

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
                draw(
                    pair.getSecond(),
                    drawArrayParameters.getMode(),
                    drawArrayParameters.getVertexFormat(),
                    drawArrayParameters.getCount(),
                    drawArrayParameters.getElementFormat(),
                    drawArrayParameters.getVertexCount(),
                    drawArrayParameters.hasNoIndexBuffer()
                );
                ((IPatchedBufferBuilder) bufferBuilder).put(pair.getSecond());
            });
        } else {
            Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
            BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
            draw(
                pair.getSecond(),
                drawArrayParameters.getMode(),
                drawArrayParameters.getVertexFormat(),
                drawArrayParameters.getCount(),
                drawArrayParameters.getElementFormat(),
                drawArrayParameters.getVertexCount(),
                drawArrayParameters.hasNoIndexBuffer()
            );
            ((IPatchedBufferBuilder) bufferBuilder).put(pair.getSecond());
        }
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("DataFlowIssue")
    @Overwrite
    private static void draw(
        ByteBuffer buffer,
        VertexFormat.DrawMode drawMode,
        VertexFormat vertexFormat,
        int count,
        VertexFormat.IntType elementFormat,
        int vertexCount,
        boolean noIndexBuffer
    ) {
        RenderSystem.assertOnRenderThread();
        buffer.clear();
        if (count <= 0) {
            return;
        }
        int vertexSize = count * vertexFormat.getVertexSize();

        bind(vertexFormat);
        buffer.position(0);
        buffer.limit(vertexSize);
        GlStateManager._glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

        int type;
        if (noIndexBuffer) {
            RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(drawMode, vertexCount);
            int indexBufferId  = indexBuffer.getId();
            if (indexBufferId != currentElementBuffer) {
                GlStateManager._glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
                currentElementBuffer = indexBufferId;
            }
            type = indexBuffer.getElementFormat().type;
        } else {
            int l = vertexFormat.getElementBuffer();
            if (l != currentElementBuffer) {
                GlStateManager._glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, l);
                currentElementBuffer = l;
            }
            buffer.position(vertexSize);
            buffer.limit(vertexSize + vertexCount * elementFormat.size);
            GlStateManager._glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
            type = elementFormat.type;
        }

        Shader shader = RenderSystem.getShader();
        for (int l = 0; l < 8; ++l) {
            int m = RenderSystem.getShaderTexture(l);
            shader.addSampler(samplerNames[l], m);
        }
        if (shader.modelViewMat != null) {
            shader.modelViewMat.set(RenderSystem.getModelViewMatrix());
        }
        if (shader.projectionMat != null) {
            shader.projectionMat.set(RenderSystem.getProjectionMatrix());
        }
        if (shader.viewRotationMat != null) {
            shader.viewRotationMat.method_39978(RenderSystem.getInverseViewRotationMatrix());
        }
        if (shader.colorModulator != null) {
            shader.colorModulator.set(RenderSystem.getShaderColor());
        }
        if (shader.fogStart != null) {
            shader.fogStart.set(RenderSystem.getShaderFogStart());
        }
        if (shader.fogEnd != null) {
            shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        }
        if (shader.fogColor != null) {
            shader.fogColor.set(RenderSystem.getShaderFogColor());
        }
        if (shader.fogShape != null) {
            shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }
        if (shader.textureMat != null) {
            shader.textureMat.set(RenderSystem.getTextureMatrix());
        }
        if (shader.gameTime != null) {
            shader.gameTime.set(RenderSystem.getShaderGameTime());
        }
        if (shader.screenSize != null) {
            Window window = MinecraftClient.getInstance().getWindow();
            shader.screenSize.set((float) window.getFramebufferWidth(), (float) window.getFramebufferHeight());
        }
        if (shader.lineWidth != null && (drawMode == VertexFormat.DrawMode.LINES || drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
            shader.lineWidth.set(RenderSystem.getShaderLineWidth());
        }

        RenderSystem.setupShaderLights(shader);
        shader.bind();
        GlStateManager._drawElements(drawMode.mode, vertexCount, type, 0L);
        shader.unbind();
        buffer.position(0);
    }
}
