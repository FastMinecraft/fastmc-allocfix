package dev.fastmc.allocfix

import java.nio.ByteBuffer

interface IPatchedBufferBuilder {
    var primitiveCenters: FloatArray?

    fun put(byteBuffer: ByteBuffer)
}