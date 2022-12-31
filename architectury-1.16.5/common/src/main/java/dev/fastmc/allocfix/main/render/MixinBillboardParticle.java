package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.util.Adapters;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BillboardParticle.class)
public abstract class MixinBillboardParticle extends Particle {
    protected MixinBillboardParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Shadow
    public abstract float getSize(float tickDelta);

    @Shadow
    protected abstract float getMinU();

    @Shadow
    protected abstract float getMaxU();

    @Shadow
    protected abstract float getMinV();

    @Shadow
    protected abstract float getMaxV();

    private static final Quaternionf QUATERNION = new Quaternionf(0.0f, 0.0f, 0.0f, 0.0f);
    private static final Vector3f VECTOR_3F = new Vector3f();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("DuplicatedCode")
    @Overwrite
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternion = QUATERNION;
        Vector3f vector3f = VECTOR_3F;

        Vec3d cameraPos = camera.getPos();

        float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());

        Adapters.toJoml(camera.getRotation(), quaternion);
        if (this.angle != 0.0f) {
            quaternion.rotateZ(MathHelper.lerp(tickDelta, this.prevAngle, this.angle));
        }

        float size = this.getSize(tickDelta);
        int brightness = this.getBrightness(tickDelta);

        float u1 = this.getMinU();
        float u2 = this.getMaxU();
        float v1 = this.getMinV();
        float v2 = this.getMaxV();

        vector3f.set(-1.0f, -1.0f, 0.0f);
        vector3f.rotate(quaternion);
        vector3f.mul(size);
        vector3f.add(x, y, z);
        vertexConsumer
            .vertex(vector3f.x, vector3f.y, vector3f.z)
            .texture(u2, v2)
            .color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
            .light(brightness)
            .next();

        vector3f.set(-1.0f, 1.0f, 0.0f);
        vector3f.rotate(quaternion);
        vector3f.mul(size);
        vector3f.add(x, y, z);
        vertexConsumer
            .vertex(vector3f.x, vector3f.y, vector3f.z)
            .texture(u2, v1)
            .color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
            .light(brightness)
            .next();

        vector3f.set(1.0f, 1.0f, 0.0f);
        vector3f.rotate(quaternion);
        vector3f.mul(size);
        vector3f.add(x, y, z);
        vertexConsumer
            .vertex(vector3f.x, vector3f.y, vector3f.z)
            .texture(u1, v1)
            .color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
            .light(brightness)
            .next();

        vector3f.set(1.0f, -1.0f, 0.0f);
        vector3f.rotate(quaternion);
        vector3f.mul(size);
        vector3f.add(x, y, z);
        vertexConsumer
            .vertex(vector3f.x, vector3f.y, vector3f.z)
            .texture(u1, v2)
            .color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
            .light(brightness)
            .next();
    }
}