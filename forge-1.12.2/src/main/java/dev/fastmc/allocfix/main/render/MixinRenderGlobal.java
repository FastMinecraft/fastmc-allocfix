package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.DummyLinkedHashSet;
import dev.fastmc.allocfix.IPatchedRenderGlobal;
import dev.fastmc.allocfix.IPatchedVisGraph;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal implements IPatchedRenderGlobal {
    @Shadow
    private List<ContainerLocalRenderInformation> renderInfos;

    @Shadow
    private Set<RenderChunk> chunksToUpdate;

    @Shadow private WorldClient world;
    private List<ContainerLocalRenderInformation> cachedRenderInfos = new ArrayList<>();

    private final ArrayDeque<?> cachedArrayDeque = new ArrayDeque<>();

    private Set<RenderChunk> cachedSet = new DummyLinkedHashSet<>(new ObjectLinkedOpenHashSet<>());



    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private Set<EnumFacing> getVisibleFacings(BlockPos pos) {
        VisGraph visgraph = new VisGraph();
        IPatchedVisGraph patchedVisGraph = (IPatchedVisGraph) visgraph;

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        Chunk chunk = this.world.getChunk(chunkX, chunkZ);

        int x1 = chunkX << 4;
        int y1 = pos.getY() >> 4 << 4;
        int z1 = chunkZ << 4;

        int x2 = x1 + 16;
        int y2 = y1 + 16;
        int z2 = z1 + 16;

        for (; x1 < x2; x1++) {
            for (; y1 < y2; y1++) {
                for (; z1 < z2; z1++) {
                    IBlockState blockState = chunk.getBlockState(x1, y1, z1);
                    if (blockState.isOpaqueCube()) {
                        //noinspection ConstantConditions
                        patchedVisGraph.setOpaqueCube(x1 & 15, y1 & 15, z1 & 15);
                    }
                }
            }
        }

        return visgraph.getVisibleFacings(pos);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$RETURN(CallbackInfo ci) {
        this.chunksToUpdate = new DummyLinkedHashSet<>(new ObjectLinkedOpenHashSet<>());
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newLinkedHashSet()Ljava/util/LinkedHashSet;", remap = false))
    private LinkedHashSet<?> Redirect$setupTerrain$INVOKE$newLinkedHashSet() {
        Set<RenderChunk> temp = cachedSet;
        cachedSet = this.chunksToUpdate;
        temp.clear();
        return (LinkedHashSet<?>) temp;
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false))
    private ArrayList<?> Redirect$setupTerrain$INVOKE$newArrayList() {
        List<ContainerLocalRenderInformation> swap = cachedRenderInfos;
        cachedRenderInfos = renderInfos;
        for (int i = swap.size() - 1; i >= 0; i--) {
            cachedRenderInfos.add(swap.remove(i));
        }
        return (ArrayList<?>) swap;
    }

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Queues;newArrayDeque()Ljava/util/ArrayDeque;", remap = false))
    public ArrayDeque<?> Redirect$setupTerrain$INVOKE$newArrayDeque() {
        assert this.cachedArrayDeque.isEmpty();
        return this.cachedArrayDeque;
    }

    @SuppressWarnings("ListRemoveInLoop")
    @Inject(method = "setupTerrain", at = @At(value = "RETURN"))
    public void Inject$setupTerrain$RETURN(
        Entity p_174970_1_,
        double p_174970_2_,
        ICamera p_174970_4_,
        int p_174970_5_,
        boolean p_174970_6_,
        CallbackInfo ci
    ) {
        int removeSize = cachedRenderInfos.size() - 67600;
        if (removeSize > 1024) {
            int start = cachedRenderInfos.size() - 1;
            int end = start - removeSize;
            for (int i = start; i > end; i--) {
                cachedRenderInfos.remove(i);
            }
        }
    }

    private final BlockPos.MutableBlockPos cachedPos1 = new BlockPos.MutableBlockPos();

    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 0))
    private BlockPos Redirect$setupTerrain$NEW$BlockPos$0(double x, double y, double z) {
        return cachedPos1.setPos(x, y, z);
    }

    private final BlockPos.MutableBlockPos cachedPos2 = new BlockPos.MutableBlockPos();

    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 1))
    private BlockPos Redirect$setupTerrain$NEW$BlockPos$1(int x, int y, int z) {
        return cachedPos2.setPos(x, y, z);
    }

    private final BlockPos.MutableBlockPos cachedPos3 = new BlockPos.MutableBlockPos();

    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 2))
    private BlockPos Redirect$setupTerrain$NEW$BlockPos$2(int x, int y, int z) {
        return cachedPos3.setPos(x, y, z);
    }

    private final BlockPos.MutableBlockPos cachedPos4 = new BlockPos.MutableBlockPos();

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;", ordinal = 0))
    private BlockPos Redirect$setupTerrain$INVOKE$BlockPos$add(BlockPos pos, int x, int y, int z) {
        return cachedPos4.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    @NotNull
    @Override
    public List<ContainerLocalRenderInformation> getCachedRenderInfos() {
        return cachedRenderInfos;
    }
}
