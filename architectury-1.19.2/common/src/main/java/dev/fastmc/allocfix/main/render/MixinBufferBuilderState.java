package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedBufferBuilderState;
import net.minecraft.client.render.BufferBuilder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BufferBuilder.State.class)
public class MixinBufferBuilderState implements IPatchedBufferBuilderState {
    private float[] primitiveCenters;

    @Override
    public float @Nullable [] getPrimitiveCenters() {
        return primitiveCenters;
    }

    @Override
    public void setPrimitiveCenters(float[] primitiveCenters) {
        this.primitiveCenters = primitiveCenters;
    }
}
