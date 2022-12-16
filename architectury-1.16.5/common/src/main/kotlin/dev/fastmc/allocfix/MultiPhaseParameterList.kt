package dev.fastmc.allocfix

import net.minecraft.client.render.RenderLayer.MultiPhaseParameters

interface IPatchedMultiPhaseParameters {
    @Suppress("CAST_NEVER_SUCCEEDS")
    val thisRef: MultiPhaseParameters
        get() = this as MultiPhaseParameters

    fun asString(): String {
        val ref = thisRef
        return buildString {
            append('[')
            append(ref.texture).append(", ")
            append(ref.transparency).append(", ")
            append(ref.diffuseLighting).append(", ")
            append(ref.shadeModel).append(", ")
            append(ref.alpha).append(", ")
            append(ref.depthTest).append(", ")
            append(ref.cull).append(", ")
            append(ref.lightmap).append(", ")
            append(ref.overlay).append(", ")
            append(ref.fog).append(", ")
            append(ref.layering).append(", ")
            append(ref.target).append(", ")
            append(ref.texturing).append(", ")
            append(ref.writeMaskState).append(", ")
            append(ref.lineWidth).append(", ")
            append(']')
        }
    }

    fun hash(): Int {
        val ref = thisRef
        var result = ref.texture.hashCode()
        result = 31 * result + ref.transparency.hashCode()
        result = 31 * result + ref.diffuseLighting.hashCode()
        result = 31 * result + ref.shadeModel.hashCode()
        result = 31 * result + ref.alpha.hashCode()
        result = 31 * result + ref.depthTest.hashCode()
        result = 31 * result + ref.cull.hashCode()
        result = 31 * result + ref.lightmap.hashCode()
        result = 31 * result + ref.overlay.hashCode()
        result = 31 * result + ref.fog.hashCode()
        result = 31 * result + ref.layering.hashCode()
        result = 31 * result + ref.target.hashCode()
        result = 31 * result + ref.texturing.hashCode()
        result = 31 * result + ref.writeMaskState.hashCode()
        result = 31 * result + ref.lineWidth.hashCode()
        return result
    }

    fun eq(other: MultiPhaseParameters): Boolean {
        val ref = thisRef
        return ref.texture == other.texture
            && ref.transparency == other.transparency
            && ref.diffuseLighting == other.diffuseLighting
            && ref.shadeModel == other.shadeModel
            && ref.alpha == other.alpha
            && ref.depthTest == other.depthTest
            && ref.cull == other.cull
            && ref.lightmap == other.lightmap
            && ref.overlay == other.overlay
            && ref.fog == other.fog
            && ref.layering == other.layering
            && ref.target == other.target
            && ref.texturing == other.texturing
            && ref.writeMaskState == other.writeMaskState
            && ref.lineWidth == other.lineWidth
    }

    fun start() {
        val ref = thisRef
        ref.texture.startDrawing()
        ref.transparency.startDrawing()
        ref.diffuseLighting.startDrawing()
        ref.shadeModel.startDrawing()
        ref.alpha.startDrawing()
        ref.depthTest.startDrawing()
        ref.cull.startDrawing()
        ref.lightmap.startDrawing()
        ref.overlay.startDrawing()
        ref.fog.startDrawing()
        ref.layering.startDrawing()
        ref.target.startDrawing()
        ref.texturing.startDrawing()
        ref.writeMaskState.startDrawing()
        ref.lineWidth.startDrawing()
    }

    fun end() {
        val ref = thisRef
        ref.texture.endDrawing()
        ref.transparency.endDrawing()
        ref.diffuseLighting.endDrawing()
        ref.shadeModel.endDrawing()
        ref.alpha.endDrawing()
        ref.depthTest.endDrawing()
        ref.cull.endDrawing()
        ref.lightmap.endDrawing()
        ref.overlay.endDrawing()
        ref.fog.endDrawing()
        ref.layering.endDrawing()
        ref.target.endDrawing()
        ref.texturing.endDrawing()
        ref.writeMaskState.endDrawing()
        ref.lineWidth.endDrawing()
    }
}