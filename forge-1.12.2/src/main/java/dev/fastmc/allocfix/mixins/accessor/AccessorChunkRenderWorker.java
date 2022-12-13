package dev.fastmc.allocfix.mixins.accessor;

import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkRenderWorker.class)
public interface AccessorChunkRenderWorker {
    @Invoker
    void callProcessTask(final ChunkCompileTaskGenerator generator) throws InterruptedException;
}
