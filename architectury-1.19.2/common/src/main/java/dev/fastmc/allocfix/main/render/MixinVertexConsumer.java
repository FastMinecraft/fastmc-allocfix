package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexConsumer.class)
public interface MixinVertexConsumer extends IPatchedVertexConsumer {

    @Shadow
    void vertex(
        float x,
        float y,
        float z,
        float red,
        float green,
        float blue,
        float alpha,
        float u,
        float v,
        int overlay,
        int light,
        float normalX,
        float normalY,
        float normalZ
    );

    @Shadow
    VertexConsumer vertex(double var1, double var3, double var5);

    @Shadow
    VertexConsumer normal(float var1, float var2, float var3);

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        Vector4f vec4f = getVec4f();
        vec4f.set(x, y, z, 1.0f);
        vec4f.transform(matrix);
        return this.vertex(vec4f.getX(), vec4f.getY(), vec4f.getZ());
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    default VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
        Vec3f vec3f = getVec3f();
        vec3f.set(x, y, z);
        vec3f.transform(matrix);
        return this.normal(vec3f.getX(), vec3f.getY(), vec3f.getZ());
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    default void quad(
        MatrixStack.Entry matrixEntry,
        BakedQuad quad,
        float red,
        float green,
        float blue,
        int light,
        int overlay
    ) {
        Vector4f vec4f = getVec4f();
        Vec3f vec3f = getVec3f();
        Vec3i faceVec = quad.getFace().getVector();

        vec3f.set(faceVec.getX(), faceVec.getY(), faceVec.getZ());
        vec3f.transform(matrixEntry.getNormalMatrix());
        Matrix4f matrix4f = matrixEntry.getPositionMatrix();

        int[] vertexData = quad.getVertexData();
        int vertexCount = vertexData.length / 8;
        for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex) {
            float v;
            float u;

            float x = Float.intBitsToFloat(vertexData[vertexIndex * 8]);
            float y = Float.intBitsToFloat(vertexData[vertexIndex * 8 + 1]);
            float z = Float.intBitsToFloat(vertexData[vertexIndex * 8 + 2]);

            u = Float.intBitsToFloat(vertexData[vertexIndex * 8 + 4]);
            v = Float.intBitsToFloat(vertexData[vertexIndex * 8 + 5]);
            vec4f.set(x, y, z, 1.0f);
            vec4f.transform(matrix4f);
            this.vertex(
                vec4f.getX(),
                vec4f.getY(),
                vec4f.getZ(),
                red,
                green,
                blue,
                1.0f,
                u,
                v,
                overlay,
                light,
                vec3f.getX(),
                vec3f.getY(),
                vec3f.getZ()
            );
        }
    }


    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    default void quad(
        MatrixStack.Entry matrixEntry,
        BakedQuad quad,
        float[] brightnesses,
        float redIn,
        float greenIn,
        float blueIn,
        int[] lights,
        int overlay,
        boolean useQuadColorData
    ) {
        quad(
            matrixEntry,
            quad,
            brightnesses[0],
            brightnesses[1],
            brightnesses[2],
            brightnesses[3],
            redIn,
            greenIn,
            blueIn,
            lights[0],
            lights[1],
            lights[2],
            lights[3],
            overlay,
            useQuadColorData
        );
    }

    @Override
    default void quad(
        MatrixStack.Entry matrixEntry,
        BakedQuad quad,
        float brightness1,
        float brightness2,
        float brightness3,
        float brightness4,
        float redIn,
        float greenIn,
        float blueIn,
        int light1,
        int light2,
        int light3,
        int light4,
        int overlay,
        boolean useQuadColorData
    ) {
        Vector4f vec4f = getVec4f();
        Vec3f vec3f = getVec3f();
        Vec3i faceVec = quad.getFace().getVector();

        vec3f.set(faceVec.getX(), faceVec.getY(), faceVec.getZ());
        vec3f.transform(matrixEntry.getNormalMatrix());
        Matrix4f matrix4f = matrixEntry.getPositionMatrix();

        int[] vertexData = quad.getVertexData();

        float blue;
        float green;
        float red;
        float v;
        float u;

        float x;
        float y;
        float z;

        int light;


        x = Float.intBitsToFloat(vertexData[0]);
        y = Float.intBitsToFloat(vertexData[1]);
        z = Float.intBitsToFloat(vertexData[2]);

        if (useQuadColorData) {
            int bits = vertexData[3];
            float vertRed = (float) (bits >>> 24 & 0xFF) / 255.0f;
            float vertGreen = (float) (bits >>> 16 & 0xFF) / 255.0f;
            float vertBlue = (float) (bits >>> 8 & 0xFF) / 255.0f;
            red = vertRed * brightness1 * redIn;
            green = vertGreen * brightness1 * greenIn;
            blue = vertBlue * brightness1 * blueIn;
        } else {
            red = brightness1 * redIn;
            green = brightness1 * greenIn;
            blue = brightness1 * blueIn;
        }

        light = light1;
        u = Float.intBitsToFloat(vertexData[4]);
        v = Float.intBitsToFloat(vertexData[5]);
        vec4f.set(x, y, z, 1.0f);
        vec4f.transform(matrix4f);
        this.vertex(
            vec4f.getX(),
            vec4f.getY(),
            vec4f.getZ(),
            red,
            green,
            blue,
            1.0f,
            u,
            v,
            overlay,
            light,
            vec3f.getX(),
            vec3f.getY(),
            vec3f.getZ()
        );


        x = Float.intBitsToFloat(vertexData[8]);
        y = Float.intBitsToFloat(vertexData[8 + 1]);
        z = Float.intBitsToFloat(vertexData[8 + 2]);

        if (useQuadColorData) {
            int bits = vertexData[8 + 3];
            float vertRed = (float) (bits >>> 24 & 0xFF) / 255.0f;
            float vertGreen = (float) (bits >>> 16 & 0xFF) / 255.0f;
            float vertBlue = (float) (bits >>> 8 & 0xFF) / 255.0f;
            red = vertRed * brightness2 * redIn;
            green = vertGreen * brightness2 * greenIn;
            blue = vertBlue * brightness2 * blueIn;
        } else {
            red = brightness2 * redIn;
            green = brightness2 * greenIn;
            blue = brightness2 * blueIn;
        }

        light = light2;
        u = Float.intBitsToFloat(vertexData[8 + 4]);
        v = Float.intBitsToFloat(vertexData[8 + 5]);
        vec4f.set(x, y, z, 1.0f);
        vec4f.transform(matrix4f);
        this.vertex(
            vec4f.getX(),
            vec4f.getY(),
            vec4f.getZ(),
            red,
            green,
            blue,
            1.0f,
            u,
            v,
            overlay,
            light,
            vec3f.getX(),
            vec3f.getY(),
            vec3f.getZ()
        );


        x = Float.intBitsToFloat(vertexData[2 * 8]);
        y = Float.intBitsToFloat(vertexData[2 * 8 + 1]);
        z = Float.intBitsToFloat(vertexData[2 * 8 + 2]);

        if (useQuadColorData) {
            int bits = vertexData[2 * 8 + 3];
            float vertRed = (float) (bits >>> 24 & 0xFF) / 255.0f;
            float vertGreen = (float) (bits >>> 16 & 0xFF) / 255.0f;
            float vertBlue = (float) (bits >>> 8 & 0xFF) / 255.0f;
            red = vertRed * brightness3 * redIn;
            green = vertGreen * brightness3 * greenIn;
            blue = vertBlue * brightness3 * blueIn;
        } else {
            red = brightness3 * redIn;
            green = brightness3 * greenIn;
            blue = brightness3 * blueIn;
        }

        light = light3;
        u = Float.intBitsToFloat(vertexData[2 * 8 + 4]);
        v = Float.intBitsToFloat(vertexData[2 * 8 + 5]);
        vec4f.set(x, y, z, 1.0f);
        vec4f.transform(matrix4f);
        this.vertex(
            vec4f.getX(),
            vec4f.getY(),
            vec4f.getZ(),
            red,
            green,
            blue,
            1.0f,
            u,
            v,
            overlay,
            light,
            vec3f.getX(),
            vec3f.getY(),
            vec3f.getZ()
        );


        x = Float.intBitsToFloat(vertexData[3 * 8]);
        y = Float.intBitsToFloat(vertexData[3 * 8 + 1]);
        z = Float.intBitsToFloat(vertexData[3 * 8 + 2]);

        if (useQuadColorData) {
            int bits = vertexData[3 * 8 + 3];
            float vertRed = (float) (bits >>> 24 & 0xFF) / 255.0f;
            float vertGreen = (float) (bits >>> 16 & 0xFF) / 255.0f;
            float vertBlue = (float) (bits >>> 8 & 0xFF) / 255.0f;
            red = vertRed * brightness4 * redIn;
            green = vertGreen * brightness4 * greenIn;
            blue = vertBlue * brightness4 * blueIn;
        } else {
            red = brightness4 * redIn;
            green = brightness4 * greenIn;
            blue = brightness4 * blueIn;
        }

        light = light4;
        u = Float.intBitsToFloat(vertexData[3 * 8 + 4]);
        v = Float.intBitsToFloat(vertexData[3 * 8 + 5]);
        vec4f.set(x, y, z, 1.0f);
        vec4f.transform(matrix4f);
        this.vertex(
            vec4f.getX(),
            vec4f.getY(),
            vec4f.getZ(),
            red,
            green,
            blue,
            1.0f,
            u,
            v,
            overlay,
            light,
            vec3f.getX(),
            vec3f.getY(),
            vec3f.getZ()
        );
    }
}
