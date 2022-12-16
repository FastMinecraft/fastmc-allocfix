package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedIVertexConsumer;
import net.minecraftforge.client.model.pipeline.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { VertexBufferConsumer.class, QuadGatheringTransformer.class, TransformerConsumer.class, UnpackedBakedQuad.Builder.class, VertexTransformer.class }, remap = false)
public class MixinIVertexConsumerImpl implements IPatchedIVertexConsumer {
    private final float[] tempDataArray = new float[4];

    @NotNull
    @Override
    public float[] getTempDataArray() {
        return tempDataArray;
    }
}
