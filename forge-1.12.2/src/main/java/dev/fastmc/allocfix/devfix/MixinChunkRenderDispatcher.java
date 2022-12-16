package dev.fastmc.allocfix.devfix;

import dev.fastmc.allocfix.accessor.AccessorChunkRenderWorker;
import dev.fastmc.allocfix.accessor.AccessorPendingUpload;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@Mixin(ChunkRenderDispatcher.class)
public class MixinChunkRenderDispatcher {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private List<Thread> listWorkerThreads;

    @Shadow
    @Final
    private PriorityBlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates;

    @Shadow
    @Final
    private ChunkRenderWorker renderWorker;

    @Shadow
    @Final
    private Queue<ChunkRenderDispatcher.PendingUpload> queueChunkUploads;

    /**
     * @author Luna
     * @reason Faster chunk upload in dev environment
     */
    @SuppressWarnings("SynchronizeOnNonFinalField")
    @Overwrite
    public boolean runChunkUploads(long finishTimeNano) {
        if (this.listWorkerThreads.isEmpty()) {
            ChunkCompileTaskGenerator generator = this.queueChunkUpdates.poll();
            while (generator != null) {
                try {
                    ((AccessorChunkRenderWorker) this.renderWorker).callProcessTask(generator);
                } catch (InterruptedException e) {
                    LOGGER.warn("Skipped task due to interrupt");
                }
                generator = this.queueChunkUpdates.poll();
            }
        }

        int uploadCount = 0;
        int targetUploadCount = this.queueChunkUploads.size() >> 2;

        synchronized (this.queueChunkUploads) {
            ChunkRenderDispatcher.PendingUpload pending = this.queueChunkUploads.poll();
            while (pending != null) {
                ((AccessorPendingUpload) pending).getUploadTask().run();
                uploadCount++;
                if (uploadCount >= targetUploadCount && System.nanoTime() >= finishTimeNano) break;
                pending = this.queueChunkUploads.poll();
            }
        }

        return uploadCount != 0;
    }
}
