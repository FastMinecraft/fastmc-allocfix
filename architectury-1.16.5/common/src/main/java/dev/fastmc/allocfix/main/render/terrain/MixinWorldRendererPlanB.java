package dev.fastmc.allocfix.main.render.terrain;

import dev.fastmc.allocfix.IPatchedWorldRenderer;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRendererPlanB implements IPatchedWorldRenderer {
    @SuppressWarnings({ "InvalidInjectorMethodSignature", "MixinAnnotationTarget", "UnresolvedMixinReference" })
    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "net/minecraft/client/render/WorldRenderer$ChunkInfo"), expect = 0)
    public WorldRenderer.ChunkInfo Redirect$setupTerrain$NEW$WorldRenderer$ChunkInfo(
        WorldRenderer thisRef,
        @Nullable ChunkBuilder.BuiltChunk chunk,
        Direction direction,
        int propagationLevel
    ) {
        ObjectList<WorldRenderer.ChunkInfo> cachedVisibleChunks = getCachedVisibleChunks();
        if (cachedVisibleChunks.isEmpty()) {
            return thisRef.new ChunkInfo(chunk, direction, propagationLevel);
        }
        WorldRenderer.ChunkInfo cached = cachedVisibleChunks.remove(cachedVisibleChunks.size() - 1);
        cached.chunk = chunk;
        cached.direction = direction;
        cached.cullingState = 0;
        cached.propagationLevel = propagationLevel;
        return cached;
    }
}
