package dev.fastmc.allocfix.mixins.main.render;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Shadow
    private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos;

    private List<RenderGlobal.ContainerLocalRenderInformation> cachedRenderInfos = new ArrayList<>();

    private final ArrayDeque<?> cachedArrayDeque = new ArrayDeque<>();

    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false))
    public ArrayList<?> Redirect$setupTerrain$INVOKE$newArrayList() {
        List<RenderGlobal.ContainerLocalRenderInformation> swap = cachedRenderInfos;
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

    @SuppressWarnings({ "UnresolvedMixinReference", "InvalidInjectorMethodSignature", "MixinAnnotationTarget" })
    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "net/minecraft/client/renderer/RenderGlobal$ContainerLocalRenderInformation"), expect = 3)
    public RenderGlobal.ContainerLocalRenderInformation Redirect$setupTerrain$NEW$ContainerLocalRenderInformation(
        RenderGlobal thisRef,
        RenderChunk renderChunkIn,
        @Nullable EnumFacing facingIn,
        int counterIn
    ) {
        if (cachedRenderInfos.isEmpty()) {
            return thisRef.new ContainerLocalRenderInformation(renderChunkIn, facingIn, counterIn);
        }
        RenderGlobal.ContainerLocalRenderInformation cached = cachedRenderInfos.remove(cachedRenderInfos.size() - 1);
        cached.renderChunk = renderChunkIn;
        cached.counter = counterIn;
        cached.facing = facingIn;
        cached.setFacing = 0;
        return cached;
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
}
