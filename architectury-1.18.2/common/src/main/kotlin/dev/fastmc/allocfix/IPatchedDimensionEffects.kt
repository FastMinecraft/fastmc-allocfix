package dev.fastmc.allocfix

import net.minecraft.client.render.DimensionEffects
import net.minecraft.util.math.Vec3d

interface IPatchedDimensionEffects {
    fun adjustFogColor(color: Int, sunHeight: Float): Int {
        this as DimensionEffects
        val result = adjustFogColor(Vec3d.unpackRgb(color), sunHeight)
        return ((result.x * 1023.0f).toInt() shl 20) or
            ((result.y * 1023.0f).toInt() shl 10) or
            (result.z * 1023.0f).toInt()
    }
}