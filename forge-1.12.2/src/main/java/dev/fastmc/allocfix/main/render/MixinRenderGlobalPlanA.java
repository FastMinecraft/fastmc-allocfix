package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedRenderGlobal;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobalPlanA implements IPatchedRenderGlobal {
    @SuppressWarnings({ "UnresolvedMixinReference", "InvalidInjectorMethodSignature", "MixinAnnotationTarget", "InvalidMemberReference" })
    @Redirect(method = "setupTerrain", at = @At(value = "NEW", target = "(Lnet/minecraft/client/renderer/RenderGlobal;Lnet/minecraft/client/renderer/chunk/RenderChunk;Lnet/minecraft/util/EnumFacing;ILnet/minecraft/client/renderer/RenderGlobal$1;)Lnet/minecraft/client/renderer/RenderGlobal$ContainerLocalRenderInformation;"), expect = 0)
    public RenderGlobal.ContainerLocalRenderInformation Redirect$setupTerrain$NEW$ContainerLocalRenderInformation(
        RenderGlobal thisRef,
        RenderChunk renderChunkIn,
        EnumFacing facingIn,
        int counterIn,
        net.minecraft.client.renderer.RenderGlobal$1 object
    ) {
        List<RenderGlobal.ContainerLocalRenderInformation> cachedRenderInfos = getCachedRenderInfos();
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
}
