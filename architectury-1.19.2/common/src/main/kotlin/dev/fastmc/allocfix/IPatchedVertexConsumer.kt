package dev.fastmc.allocfix

import com.google.common.collect.MapMaker
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f

interface IPatchedVertexConsumer {
    val vec4f: Vector4f
        get() = get(this).second
    val vec3f: Vec3f
        get() = get(this).first

    fun quad(
        matrixEntry: MatrixStack.Entry,
        quad: BakedQuad,
        brightness1: Float,
        brightness2: Float,
        brightness3: Float,
        brightness4: Float,
        redIn: Float,
        greenIn: Float,
        blueIn: Float,
        light1: Int,
        light2: Int,
        light3: Int,
        light4: Int,
        overlay: Int,
        useQuadColorData: Boolean
    )

    companion object {
        private val map = MapMaker().weakKeys().makeMap<IPatchedVertexConsumer, Pair<Vec3f, Vector4f>>()

        fun get(consumer: IPatchedVertexConsumer): Pair<Vec3f, Vector4f> {
            return map.computeIfAbsent(consumer) { Pair(Vec3f(), Vector4f()) }
        }
    }
}