package dev.fastmc.allocfix.main.render.terrain;

import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelRenderer.AmbientOcclusionCalculator.class)
public class MixinAmbientOcclusionCalculator {
    private final BlockPos.Mutable mutable = new BlockPos.Mutable();

    @SuppressWarnings({ "MixinAnnotationTarget", "InvalidInjectorMethodSignature", "UnresolvedMixinReference" })
    @Redirect(method = "apply", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos$Mutable"))
    private BlockPos.Mutable Redirect$apply$NEWBlockPos$Mutable() {
        return mutable;
    }
}
