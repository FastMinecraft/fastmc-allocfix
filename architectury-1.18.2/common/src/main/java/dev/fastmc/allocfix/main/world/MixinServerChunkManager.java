package dev.fastmc.allocfix.main.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
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

    private final ArrayList<ServerChunkManager.ChunkWithHolder> list = new ArrayList<>();
    private final ObjectArrayList<ServerChunkManager.ChunkWithHolder> cachedObjs = new ObjectArrayList<>();

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;", remap = false))
    private ArrayList<?> tickChunks$Redirect$INVOKE$newArrayList(int initialArraySize) {
        if (list.size() < 65536) {
            if (cachedObjs.size() + list.size() < 65536) {
                cachedObjs.addAll(list);
            } else {
                int count = 65536 - list.size();
                for (int i = 0; i < count; i++) {
                    cachedObjs.add(list.get(i));
                }
            }
        }
        this.list.clear();
        this.list.ensureCapacity(Math.max(this.threadedAnvilChunkStorage.getLoadedChunkCount(), initialArraySize));
        return this.list;
    }

    @SuppressWarnings({ "InvalidInjectorMethodSignature", "UnresolvedMixinReference", "MixinAnnotationTarget" })
    @Redirect(method = "tickChunks", at = @At(value = "NEW", target = "net/minecraft/server/world/ServerChunkManager$ChunkWithHolder"))
    private ServerChunkManager.ChunkWithHolder tickChunks$Redirect$NEW$ChunkWithHolder(WorldChunk chunk, ChunkHolder holder) {
        if (cachedObjs.isEmpty()) {
            return new ServerChunkManager.ChunkWithHolder(chunk, holder);
        } else {
            ServerChunkManager.ChunkWithHolder result = cachedObjs.remove(cachedObjs.size() - 1);
            result.chunk = chunk;
            result.holder = holder;
            return result;
        }
    }
}
