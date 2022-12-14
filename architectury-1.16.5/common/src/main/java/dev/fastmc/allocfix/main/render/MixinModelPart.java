package dev.fastmc.allocfix.main.render;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.class)
public class MixinModelPart {
    @Shadow
    @Final
    private ObjectList<ModelPart.Cuboid> cuboids;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings({ "DuplicatedCode", "ForLoopReplaceableByForEach" })
    @Overwrite
    private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();

        Vector4f vector4f = new Vector4f();
        Vec3f vec3f = new Vec3f();

        ObjectList<ModelPart.Cuboid> cuboidObjectList = this.cuboids;
        for (int i = 0; i < cuboidObjectList.size(); i++) {
            ModelPart.Cuboid cuboid = cuboidObjectList.get(i);
            ModelPart.Quad[] sides = cuboid.sides;

            for (int j = 0; j < sides.length; ++j) {
                ModelPart.Quad quad = sides[j];
                vec3f.set(quad.direction.getX(), quad.direction.getY(), quad.direction.getZ());
                vec3f.transform(matrix3f);
                float normalX = vec3f.getX();
                float normalY = vec3f.getY();
                float normalZ = vec3f.getZ();

                ModelPart.Vertex vertex = quad.vertices[0];
                vector4f.set(vertex.pos.getX() / 16.0F, vertex.pos.getY() / 16.0F, vertex.pos.getZ() / 16.0F, 1.0f);
                vector4f.transform(matrix4f);
                vertexConsumer.vertex(
                    vector4f.getX(),
                    vector4f.getY(),
                    vector4f.getZ(),
                    red,
                    green,
                    blue,
                    alpha,
                    vertex.u,
                    vertex.v,
                    overlay,
                    light,
                    normalX,
                    normalY,
                    normalZ
                );

                vertex = quad.vertices[1];
                vector4f.set(vertex.pos.getX() / 16.0F, vertex.pos.getY() / 16.0F, vertex.pos.getZ() / 16.0F, 1.0f);
                vector4f.transform(matrix4f);
                vertexConsumer.vertex(
                    vector4f.getX(),
                    vector4f.getY(),
                    vector4f.getZ(),
                    red,
                    green,
                    blue,
                    alpha,
                    vertex.u,
                    vertex.v,
                    overlay,
                    light,
                    normalX,
                    normalY,
                    normalZ
                );

                vertex = quad.vertices[2];
                vector4f.set(vertex.pos.getX() / 16.0F, vertex.pos.getY() / 16.0F, vertex.pos.getZ() / 16.0F, 1.0f);
                vector4f.transform(matrix4f);
                vertexConsumer.vertex(
                    vector4f.getX(),
                    vector4f.getY(),
                    vector4f.getZ(),
                    red,
                    green,
                    blue,
                    alpha,
                    vertex.u,
                    vertex.v,
                    overlay,
                    light,
                    normalX,
                    normalY,
                    normalZ
                );

                vertex = quad.vertices[3];
                vector4f.set(vertex.pos.getX() / 16.0F, vertex.pos.getY() / 16.0F, vertex.pos.getZ() / 16.0F, 1.0f);
                vector4f.transform(matrix4f);
                vertexConsumer.vertex(
                    vector4f.getX(),
                    vector4f.getY(),
                    vector4f.getZ(),
                    red,
                    green,
                    blue,
                    alpha,
                    vertex.u,
                    vertex.v,
                    overlay,
                    light,
                    normalX,
                    normalY,
                    normalZ
                );
            }
        }

    }
}
