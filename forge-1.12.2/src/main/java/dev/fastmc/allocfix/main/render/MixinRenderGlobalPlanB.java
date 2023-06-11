package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedRenderGlobal;
import dev.fastmc.allocfix.UtilsKt;
import dev.fastmc.allocfix.accessor.AccessorContainerLocalRenderInformation;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobalPlanB implements IPatchedRenderGlobal {
    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "(Lnet/minecraft/client/renderer/RenderGlobal;Lnet/minecraft/client/renderer/chunk/RenderChunk;Lnet/minecraft/util/EnumFacing;I)Lnet/minecraft/client/renderer/RenderGlobal$ContainerLocalRenderInformation;"), expect = 0)
    public RenderGlobal.ContainerLocalRenderInformation Redirect$setupTerrain$NEW$ContainerLocalRenderInformation(
        RenderGlobal thisRef,
        RenderChunk renderChunkIn,
        EnumFacing facingIn,
        int counterIn
    ) {
        List<RenderGlobal.ContainerLocalRenderInformation> cachedRenderInfos = getCachedRenderInfos();
        if (cachedRenderInfos.isEmpty()) {
            return thisRef.new ContainerLocalRenderInformation(renderChunkIn, facingIn, counterIn);
        }
        RenderGlobal.ContainerLocalRenderInformation cached = cachedRenderInfos.remove(cachedRenderInfos.size() - 1);
        UtilsKt.setContainerLocalRenderInformation(cached, renderChunkIn, facingIn, (byte) 0, counterIn);

        return cached;
    }
}
