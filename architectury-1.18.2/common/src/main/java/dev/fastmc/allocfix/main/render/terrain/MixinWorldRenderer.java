package dev.fastmc.allocfix.main.render.terrain;

import dev.fastmc.allocfix.DummyLinkedHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;", remap = false))
    private HashSet<?> Redirect$init$INVOKE$Sets$newHashSet() {
        return new DummyLinkedHashSet<>(new ObjectOpenHashSet<>());
    }

    private final ArrayDeque<?> cachedDeque = new ArrayDeque<>();

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Queues;newArrayDeque()Ljava/util/ArrayDeque;", remap = false))
    private ArrayDeque<?> Redirect$setupTerrain$INVOKE$Queues$newArrayDeque() {
        cachedDeque.clear();
        return cachedDeque;
    }

    private final ArrayList<?> cachedArrayList = new ArrayList<>();

    @Redirect(method = "method_38549", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false))
    private ArrayList<?> Redirect$setupTerrain$INVOKE$Lists$newArrayList() {
        cachedArrayList.clear();
        return cachedArrayList;
    }
    private final BlockPos.Mutable cachedPos1 = new BlockPos.Mutable();

    @Redirect(method = "method_38549", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 0))
    private BlockPos Redirect$setupTerrain$NEW$BlockPos$0(int x, int y, int z) {
        return cachedPos1.set(x, y, z);
    }
}
