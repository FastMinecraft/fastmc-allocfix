package dev.fastmc.allocfix.mixins

interface IPatchedIVertexConsumer {
    val tempDataArray: FloatArray
        get() = FloatArray(4)
}