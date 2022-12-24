package dev.fastmc.allocfix.main.render.terrain;

import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelRenderer.AmbientOcclusionCalculator.class)
public class MixinAmbientOcclusionCalculator {
    private final BlockPos.Mutable mutable1 = new BlockPos.Mutable();
    private final BlockPos.Mutable mutable2 = new BlockPos.Mutable();

    @SuppressWarnings({ "MixinAnnotationTarget", "InvalidInjectorMethodSignature", "UnresolvedMixinReference" })
    @Redirect(method = "apply", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos$Mutable"))
    private BlockPos.Mutable Redirect$apply$NEWBlockPos$Mutable() {
        return mutable1;
    }

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos Redirect$apply$INVOKE$offset(BlockPos pos, net.minecraft.util.math.Direction direction) {
        return mutable2.set(pos, direction);
    }
}
