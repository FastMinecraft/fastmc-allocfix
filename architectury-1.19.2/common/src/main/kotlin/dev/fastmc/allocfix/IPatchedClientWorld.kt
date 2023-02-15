package dev.fastmc.allocfix

import net.minecraft.util.math.Vec3d

interface IPatchedClientWorld {
    fun getSkyColor10Bit(cameraPos: Vec3d, tickDelta: Float): Int
}