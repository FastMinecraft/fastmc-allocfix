package dev.fastmc.allocfix

import it.unimi.dsi.fastutil.objects.ObjectList
import net.minecraft.client.render.WorldRenderer

interface IPatchedWorldRenderer {
    val  cachedVisibleChunks: ObjectList<WorldRenderer.ChunkInfo>
}