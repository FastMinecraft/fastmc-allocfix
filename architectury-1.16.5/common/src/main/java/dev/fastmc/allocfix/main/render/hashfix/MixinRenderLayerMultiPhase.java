package dev.fastmc.allocfix.main.render.hashfix;

import dev.fastmc.allocfix.IPatchedMultiPhaseParameters;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLayer.MultiPhase.class)
public class MixinRenderLayerMultiPhase extends RenderLayer {
    @Shadow
    @Final
    private MultiPhaseParameters phases;

    @Mutable
    @Shadow
    @Final
    private static ObjectOpenCustomHashSet<MultiPhase> CACHE;

    private final static Long2ObjectOpenHashMap<RenderLayer.MultiPhase> CACHE_MAP = new Long2ObjectOpenHashMap<>();

    public MixinRenderLayerMultiPhase(
        String name,
        VertexFormat vertexFormat,
        int drawMode,
        int expectedBufferSize,
        boolean hasCrumbling,
        boolean translucent,
        Runnable startAction,
        Runnable endAction
    ) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;hashCode()I"))
    private int Redirect$init$INVOKE$RenderLayer$hashCode(RenderLayer instance) {
        return 0;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Objects;hash([Ljava/lang/Object;)I"))
    private int Redirect$init$INVOKE$Objects$hash(Object... values) {
        int result = name.hashCode();
        result = 31 * result + phases.hashCode();
        return result;
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void Redirect$init$RETURN(
        String name,
        VertexFormat vertexFormat,
        int drawMode,
        int expectedBufferSize,
        boolean hasCrumbling,
        boolean translucent,
        MultiPhaseParameters phases,
        CallbackInfo ci
    ) {
        IPatchedMultiPhaseParameters patched = (IPatchedMultiPhaseParameters) (Object) phases;
        this.beginAction = patched::start;
        this.endAction = patched::end;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void Inject$clinit$RETURN(CallbackInfo ci) {
        CACHE = null;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static MultiPhase of(
        String name,
        VertexFormat vertexFormat,
        int drawMode,
        int expectedBufferSize,
        boolean hasCrumbling,
        boolean translucent,
        MultiPhaseParameters phases
    ) {
        long hash = name.hashCode();
        hash = 31 * hash + phases.hashCode();
        RenderLayer.MultiPhase layer = CACHE_MAP.get(hash);
        if (layer == null) {
            layer = new RenderLayer.MultiPhase(
                name,
                vertexFormat,
                drawMode,
                expectedBufferSize,
                hasCrumbling,
                translucent,
                phases
            );
            CACHE_MAP.put(hash, layer);
        }
        return layer;
    }
}
