package dev.fastmc.allocfix.mixins

import net.minecraft.client.render.block.BlockModelRenderer
import net.minecraft.util.math.BlockPos
import java.util.BitSet

interface IPatchedChunkRendererRegion {
    fun getAmbientOcclusionCalculator(renderer: BlockModelRenderer): BlockModelRenderer.AmbientOcclusionCalculator
    val bitSet: BitSet
    val boxDimension: FloatArray
    val brightness: FloatArray
    val lights: IntArray
}