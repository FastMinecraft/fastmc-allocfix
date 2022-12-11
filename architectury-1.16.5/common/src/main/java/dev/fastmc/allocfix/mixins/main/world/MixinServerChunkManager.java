package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.accessor.AccessorThreadedAnvilChunkStorage;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

@Mixin(ServerChunkManager.class)
public class MixinServerChunkManager {
    @Shadow
    @Final
    public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    private final ArrayList<ChunkHolder> cachedList = new ArrayList<>();

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;", remap = false))
    private ArrayList<?> tickChunks$Redirect$INVOKE$newArrayList(Iterable<?> iterable) {
        this.cachedList.clear();
        this.cachedList.ensureCapacity(this.threadedAnvilChunkStorage.getLoadedChunkCount());
        for (ChunkHolder chunkHolder : ((AccessorThreadedAnvilChunkStorage) this.threadedAnvilChunkStorage).callEntryIterator()) {
            this.cachedList.add(chunkHolder);
        }
        return this.cachedList;
    }
}
