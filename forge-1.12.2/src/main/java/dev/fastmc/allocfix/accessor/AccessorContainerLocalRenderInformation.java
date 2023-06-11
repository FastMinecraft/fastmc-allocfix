package dev.fastmc.allocfix.accessor;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderGlobal.ContainerLocalRenderInformation.class)
public interface AccessorContainerLocalRenderInformation {
    @Accessor
    RenderChunk getRenderChunk();

    @Accessor
    void setRenderChunk(RenderChunk renderChunk);

    @Accessor
    EnumFacing getFacing();

    @Accessor
    void setFacing(EnumFacing facing);

    @Accessor
    byte getSetFacing();

    @Accessor
    void setSetFacing(byte setFacing);

    @Accessor
    int getCounter();

    @Accessor
    void setCounter(int counter);
}
