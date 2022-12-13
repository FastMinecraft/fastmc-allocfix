package dev.fastmc.allocfix.mixins

import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation

interface IPatchedRenderGlobal {
    val cachedRenderInfos: List<ContainerLocalRenderInformation>
}