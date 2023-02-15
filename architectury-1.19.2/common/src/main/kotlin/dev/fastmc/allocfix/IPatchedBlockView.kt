package dev.fastmc.allocfix

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

interface IPatchedBlockView {
    val thisRef: BlockView; get() = this as BlockView

    fun getBlockState(x: Int, y: Int, z: Int): BlockState {
        return thisRef.getBlockState(BlockPos(x, y, z))
    }
}