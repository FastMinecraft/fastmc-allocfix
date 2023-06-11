package dev.fastmc.allocfix.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net/minecraftforge/client/ForgeHooksClient$LightGatheringTransformer", remap = false)
public interface AccessorLightGatheringTransformer {
    @Accessor
    int getBlockLight();
    @Accessor
    int getSkyLight();

    @Invoker
    boolean callHasLighting();
}
