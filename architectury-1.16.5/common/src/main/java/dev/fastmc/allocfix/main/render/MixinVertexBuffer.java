package dev.fastmc.allocfix.main.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;

@Mixin(VertexBuffer.class)
public class MixinVertexBuffer {
    @Shadow private int vertexBufferId;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;glGenBuffers(Ljava/util/function/Consumer;)V"))
    private void Redirect$init$INVOKE$glGenBuffers(Consumer<Integer> consumer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.vertexBufferId = GlStateManager.genBuffers());
        } else {
            this.vertexBufferId = GlStateManager.genBuffers();
        }
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void bind() {
        GlStateManager.bindBuffers(GL_ARRAY_BUFFER, this.vertexBufferId);
    }
}
