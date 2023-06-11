package dev.fastmc.allocfix

import dev.fastmc.allocfix.accessor.AccessorContainerLocalRenderInformation
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation
import net.minecraft.client.renderer.chunk.RenderChunk
import net.minecraft.util.EnumFacing

fun setContainerLocalRenderInformation(
    container: ContainerLocalRenderInformation,
    renderChunk: RenderChunk,
    facing: EnumFacing?,
    setFacing: Byte,
    counter: Int,
) {
    container as AccessorContainerLocalRenderInformation
    container.renderChunk = renderChunk
    container.facing = facing
    container.setFacing = setFacing
    container.counter = counter
}