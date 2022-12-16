package dev.fastmc.allocfix

import java.nio.ByteBuffer

interface IPatchedBufferBuilder {
    fun put(byteBuffer: ByteBuffer)
}