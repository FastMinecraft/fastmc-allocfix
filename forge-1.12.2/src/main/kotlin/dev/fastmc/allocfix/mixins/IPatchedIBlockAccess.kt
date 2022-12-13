package dev.fastmc.allocfix.mixins

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

interface IPatchedIBlockAccess {
    val thisRef: IBlockAccess; get() = this as IBlockAccess

    fun getBlockState(x: Int, y: Int, z: Int): IBlockState {
        return thisRef.getBlockState(BlockPos(x, y, z))
    }

    fun getCombinedLight(x: Int, y: Int, z: Int, lightValue: Int): Int {
        return thisRef.getCombinedLight(BlockPos(x, y, z), lightValue)
    }
}