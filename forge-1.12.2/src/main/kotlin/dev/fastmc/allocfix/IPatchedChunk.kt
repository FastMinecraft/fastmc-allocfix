package dev.fastmc.allocfix

import net.minecraft.world.EnumSkyBlock

interface IPatchedChunk {
    fun getLightFor(type: EnumSkyBlock, x: Int, y: Int, z: Int): Int

    fun canSeeSky(x: Int, y: Int, z: Int): Boolean
}