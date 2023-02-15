package dev.fastmc.allocfix

import net.minecraft.block.BlockState

interface IPatchedRenderedChunk {
    fun getBlockState(x: Int, y: Int, z: Int): BlockState
}