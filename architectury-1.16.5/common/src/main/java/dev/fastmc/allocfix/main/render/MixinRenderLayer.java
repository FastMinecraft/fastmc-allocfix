package dev.fastmc.allocfix.main.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.Objects;

@Mixin(RenderLayer.class)
public class MixinRenderLayer {
    private static final List<RenderLayer> blockLayers = ImmutableList.of(RenderLayer.getSolid(), RenderLayer.getCutoutMipped(), RenderLayer.getCutout(), RenderLayer.getTranslucent(), RenderLayer.getTripwire());

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static List<RenderLayer> getBlockLayers() {
        return blockLayers;
    }
}
