package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedIVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = IVertexConsumer.class, remap = false)
public interface MixinIPatchedIVertexConsumer extends IPatchedIVertexConsumer {}
