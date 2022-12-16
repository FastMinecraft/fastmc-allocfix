package dev.fastmc.allocfix.accessor;

import com.google.common.util.concurrent.ListenableFutureTask;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkRenderDispatcher.PendingUpload.class)
public interface AccessorPendingUpload {
    @Accessor
    ListenableFutureTask<Object> getUploadTask();
}
