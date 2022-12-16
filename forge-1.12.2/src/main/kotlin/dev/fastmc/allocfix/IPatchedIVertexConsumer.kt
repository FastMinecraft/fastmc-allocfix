package dev.fastmc.allocfix

interface IPatchedIVertexConsumer {
    val tempDataArray: FloatArray
        get() = FloatArray(4)
}