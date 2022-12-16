package dev.fastmc.allocfix


import dev.fastmc.allocfix.accessor.AccessorVoxelShape
import dev.fastmc.common.BYTE_FALSE
import dev.fastmc.common.BYTE_TRUE
import dev.fastmc.common.BYTE_UNCHECKED
import dev.fastmc.common.collection.*
import net.minecraft.block.BlockState
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.SlicedVoxelShape
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

@Suppress("NOTHING_TO_INLINE")
class BlockCullingCache {
    private val coveredSideCache = Int2ByteCacheMap(512, BYTE_UNCHECKED)
    private val matchVoxelShapeCache = Int2ByteCacheMap(2048, BYTE_UNCHECKED)
    private val cullingFaceCache = Int2ObjectCacheMap<VoxelShape>(2048)

    fun isSideCovered(
        shape: VoxelShape,
        neighbor: VoxelShape,
        direction: Direction
    ): Boolean {
        return when {
            shape === VoxelShapes.fullCube() && neighbor === VoxelShapes.fullCube() -> {
                true
            }
            neighbor.isEmpty -> {
                false
            }
            else -> {
                var hash = (shape as IPatchedVoxelShape).hash()
                hash = hash * 31 + (neighbor as IPatchedVoxelShape).hash()
                hash = hash * 31 + direction.ordinal

                when (coveredSideCache.get(hash)) {
                    BYTE_TRUE -> {
                        true
                    }
                    BYTE_FALSE -> {
                        false
                    }
                    else -> {
                        val axis = direction.axis
                        val isPositive = direction.direction == Direction.AxisDirection.POSITIVE
                        val a: VoxelShape
                        val b: VoxelShape

                        if (isPositive) {
                            a = shape
                            b = neighbor
                        } else {
                            a = neighbor
                            b = shape
                        }

                        if (!fuzzyEqualSq(a.getMax(axis), 1.0, 0.000025)
                            || !fuzzyEqualSq(b.getMin(axis), 0.0, 0.000025)
                        ) {
                            return false
                        }

                        val slicedA = SlicedVoxelShape(a, axis, (a as AccessorVoxelShape).voxels.getSize(axis) - 1)
                        val slicedB = SlicedVoxelShape(b, axis, 0)

                        val result = if (isPositive) {
                            !matchVoxelShape(slicedA, slicedB)
                        } else {
                            !matchVoxelShape(slicedB, slicedA)
                        }
                        coveredSideCache.put(hash, result)
                        result
                    }
                }
            }
        }
    }

    private inline fun fuzzyEqualSq(a: Double, b: Double, tolerance: Double): Boolean {
        val c = a - b
        return c * c <= tolerance
    }

    @Suppress("DEPRECATION")
    fun shouldDrawSide(
        blockView: BlockView,
        blockX: Int,
        blockY: Int,
        blockZ: Int,
        self: BlockState,
        direction: Direction
    ): Boolean {
        val otherX = blockX + direction.offsetX
        val otherY = blockY + direction.offsetY
        val otherZ = blockZ + direction.offsetZ

        val other = (blockView as IPatchedBlockView).getBlockState(otherX, otherY, otherZ)
        val otherDirection = direction.opposite

        val selfResult = if (self.block.isSideInvisible(self, other, direction)) {
            false
        } else if (other.isOpaque) {
            val selfShape = getCullingFace(blockView, blockX, blockY, blockZ, self, direction)
            val otherShape = getCullingFace(blockView, otherX, otherY, otherZ, other, otherDirection)
            matchVoxelShape(selfShape, otherShape)
        } else {
            true
        }

        return selfResult
    }

    private val cullingFaceTempPos = BlockPos.Mutable()

    private fun getCullingFace(
        blockView: BlockView,
        x: Int,
        y: Int,
        z: Int,
        state: BlockState,
        direction: Direction
    ): VoxelShape {
        var result: VoxelShape? = null
        val shapeCache = state.shapeCache
        if (shapeCache != null) {
            val extrudedFaces = shapeCache.extrudedFaces
            if (extrudedFaces != null) {
                result = extrudedFaces[direction.ordinal]
            }
        }

        if (result == null) {
            val shape = state.getCullingShape(blockView, cullingFaceTempPos.set(x, y, z))
            val hash = (shape as IPatchedVoxelShape).hash() * 31 + direction.ordinal
            result = cullingFaceCache.get(hash)
            if (result == null) {
                result = VoxelShapes.extrudeFace(shape, direction)!!
                cullingFaceCache.put(hash, result)
            }
        }

        return result
    }

    private fun matchVoxelShape(a: VoxelShape, b: VoxelShape): Boolean {
        if (a === VoxelShapes.fullCube() && b === VoxelShapes.fullCube()) return false

        val hashAB = hash(a, b)
        return when (matchVoxelShapeCache.get(hashAB)) {
            BYTE_TRUE -> {
                true
            }
            BYTE_FALSE -> {
                false
            }
            else -> {
                val hashBA = hash(b, a)
                val resultAB = VoxelShapes.matchesAnywhere(a, b, BooleanBiFunction.ONLY_FIRST)
                val resultBA = VoxelShapes.matchesAnywhere(b, a, BooleanBiFunction.ONLY_FIRST)
                matchVoxelShapeCache.put(hashBA, resultBA)
                matchVoxelShapeCache.put(hashAB, resultAB)
                resultAB
            }
        }
    }

    private inline fun hash(a: VoxelShape, b: VoxelShape): Int {
        return (a as IPatchedVoxelShape).hash() * 31 + (b as IPatchedVoxelShape).hash()
    }

    companion object {
        private val instances = ThreadLocal.withInitial { BlockCullingCache() }

        @JvmStatic
        fun getInstance(): BlockCullingCache {
            return instances.get()
        }
    }
}