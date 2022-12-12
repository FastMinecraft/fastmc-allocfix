package dev.fastmc.allocfix.mixins

import com.google.common.collect.MapMaker
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f

interface IPatchedVertexConsumer {
    val vec4f: Vector4f
        get() = get(this).second
    val vec3f: Vec3f
        get() = get(this).first

    companion object {
        private val map = MapMaker().weakKeys().makeMap<IPatchedVertexConsumer, Pair<Vec3f, Vector4f>>()

        fun get(consumer: IPatchedVertexConsumer): Pair<Vec3f, Vector4f> {
            return map.computeIfAbsent(consumer) { Pair(consumer.vec3f, consumer.vec4f) }
        }
    }
}