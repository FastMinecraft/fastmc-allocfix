package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.mixins.IPatchedIVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IVertexConsumer.class)
public interface MixinIPatchedIVertexConsumer extends IPatchedIVertexConsumer {}
