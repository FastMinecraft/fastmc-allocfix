package dev.fastmc.allocfix

interface IPatchedMatrixStack {
    fun rotate(angle: Float, x: Float, y: Float, z: Float)
    fun rotateX(angle: Float)
    fun rotateY(angle: Float)
    fun rotateZ(angle: Float)
}