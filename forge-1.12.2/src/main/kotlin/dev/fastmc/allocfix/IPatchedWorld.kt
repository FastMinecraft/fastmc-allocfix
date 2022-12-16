package dev.fastmc.allocfix

import net.minecraft.world.EnumSkyBlock

interface IPatchedWorld : IPatchedIBlockAccess {
    fun checkLightFor(lightType: EnumSkyBlock, x: Int, y: Int, z: Int): Boolean
    fun getLightFromNeighborsFor(type: EnumSkyBlock, x: Int, y: Int, z: Int): Int
    fun getLightFor(type: EnumSkyBlock, x: Int, y: Int, z: Int): Int
    fun isValid(x: Int, y: Int, z: Int): Boolean
    fun isOutsideBuildHeight(y: Int): Boolean
    fun isBlockLoaded(x: Int, z: Int): Boolean
    fun isBlockLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean
    fun isAreaLoaded(x: Int, y: Int, z: Int, radius: Int, allowEmpty: Boolean): Boolean
}