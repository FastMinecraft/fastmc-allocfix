package dev.fastmc.allocfix.main.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexBuffer.class)
public abstract class MixinVertexBuffer {
    @Shadow
    public abstract void drawElements();

    @Shadow
    private VertexFormat.DrawMode drawMode;

    private static final String[] samplerNames = new String[]{
        "Sampler0",
        "Sampler1",
        "Sampler2",
        "Sampler3",
        "Sampler4",
        "Sampler5",
        "Sampler6",
        "Sampler7",
        "Sampler8",
        "Sampler9",
        "Sampler10",
        "Sampler11",
    };

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private void drawInternal(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
        for (int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            shader.addSampler(samplerNames[i], j);
        }

        if (shader.modelViewMat != null) {
            shader.modelViewMat.set(viewMatrix);
        }

        if (shader.projectionMat != null) {
            shader.projectionMat.set(projectionMatrix);
        }

        if (shader.viewRotationMat != null) {
            shader.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());
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

        if (shader.lineWidth != null && (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
            shader.lineWidth.set(RenderSystem.getShaderLineWidth());
        }

        RenderSystem.setupShaderLights(shader);
        shader.bind();
        this.drawElements();
        shader.unbind();
    }
}
