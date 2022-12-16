package dev.fastmc.allocfix.main.render.terrain;

import dev.fastmc.allocfix.DummyLinkedHashSet;
import dev.fastmc.allocfix.IPatchedWorldRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements IPatchedWorldRenderer {
    @Mutable
    @Shadow
    @Final
    private ObjectList<WorldRenderer.ChunkInfo> visibleChunks;

    @Shadow
    private Set<ChunkBuilder.BuiltChunk> chunksToRebuild;

    private ObjectList<WorldRenderer.ChunkInfo> cachedVisibleChunks = new ObjectArrayList<>();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newLinkedHashSet()Ljava/util/LinkedHashSet;", remap = false))
    private LinkedHashSet<?> Redirect$init$INVOKE$Sets$newLinkedHashSet() {
        return new DummyLinkedHashSet<>(new ObjectLinkedOpenHashSet<>());
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;", remap = false))
    private HashSet<?> Redirect$init$INVOKE$Sets$newHashSet() {
        return new DummyLinkedHashSet<>(new ObjectOpenHashSet<>());
    }

    @Inject(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectList;clear()V", shift = At.Shift.BEFORE, remap = false, ordinal = 0))
    private void Inject$setupTerrain$INVOKE$ObjectList$clear$0(CallbackInfo ci) {
        ObjectList<WorldRenderer.ChunkInfo> temp = cachedVisibleChunks;
        cachedVisibleChunks = visibleChunks;
        visibleChunks = temp;
    }

    @SuppressWarnings({ "SingleStatementInBlock", "ListRemoveInLoop" })
    @Inject(method = "setupTerrain", at = @At("RETURN"))
    private void Inject$setupTerrain$RETURN(CallbackInfo ci) {
        int removeSize = cachedVisibleChunks.size() - 67600;
        if (removeSize > 1024) {
            int start = cachedVisibleChunks.size() - 1;
            int end = start - removeSize;
            for (int i = start; i > end; i--) {
                cachedVisibleChunks.remove(i);
            }
        }
    }

    private final ArrayDeque<?> cachedDeque = new ArrayDeque<>();

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Queues;newArrayDeque()Ljava/util/ArrayDeque;", remap = false))
    private ArrayDeque<?> Redirect$setupTerrain$INVOKE$Queues$newArrayDeque() {
        cachedDeque.clear();
        return cachedDeque;
    }

    private final ArrayList<?> cachedArrayList = new ArrayList<>();

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false))
    private ArrayList<?> Redirect$setupTerrain$INVOKE$Lists$newArrayList() {
        cachedArrayList.clear();
        return cachedArrayList;
    }

    private Set<?> cachedLinkedHashSet = new DummyLinkedHashSet<>(new ObjectLinkedOpenHashSet<>());

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newLinkedHashSet()Ljava/util/LinkedHashSet;", remap = false))
    private LinkedHashSet<?> Redirect$setupTerrain$INVOKE$Sets$newLinkedHashSet() {
        Set<?> temp = cachedLinkedHashSet;
        cachedLinkedHashSet = this.chunksToRebuild;
        temp.clear();
        return (LinkedHashSet<?>) temp;
    }

    private final BlockPos.Mutable cachedPos1 = new BlockPos.Mutable();

    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 0))
    private BlockPos Redirect$setupTerrain$NEW$BlockPos$0(int x, int y, int z) {
        return cachedPos1.set(x, y, z);
    }

    private final BlockPos.Mutable cachedPos2 = new BlockPos.Mutable();

    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 1))
    private BlockPos Redirect$setupTerrain$NEW$BlockPos$1(int x, int y, int z) {
        return cachedPos2.set(x, y, z);
    }

    private final BlockPos.Mutable cachedPos3 = new BlockPos.Mutable();

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;", ordinal = 0))
    private BlockPos Redirect$setupTerrainINVOKE$BlockPos$add$0(BlockPos instance, int x, int y, int z) {
        return cachedPos3.set(instance, x, y, z);
    }

    @NotNull
    @Override
    public ObjectList<WorldRenderer.ChunkInfo> getCachedVisibleChunks() {
        return cachedVisibleChunks;
    }
}
