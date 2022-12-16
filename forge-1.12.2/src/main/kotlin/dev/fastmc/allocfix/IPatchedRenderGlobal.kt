package dev.fastmc.allocfix

import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation

interface IPatchedRenderGlobal {
    val cachedRenderInfos: List<ContainerLocalRenderInformation>
}