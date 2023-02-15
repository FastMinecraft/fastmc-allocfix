package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.util.Adapters;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frustum.class)
public class MixinFrustum {
    @Shadow
    private Vector4f field_34821;
    private FrustumIntersection jomlFrustum = new FrustumIntersection();

    @Inject(method = "<init>(Lnet/minecraft/client/render/Frustum;)V", at = @At("RETURN"))
    private void Inject$init$RETURN(Frustum frustum, CallbackInfo ci) {
        jomlFrustum = ((MixinFrustum) (Object) frustum).jomlFrustum;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private void init(Matrix4f modelViewIn, Matrix4f projectionIn) {
        org.joml.Matrix4f matrix = Adapters.toJoml(projectionIn).mul(Adapters.toJoml(modelViewIn));
        jomlFrustum.set(matrix, false);
        matrix.transpose();
        org.joml.Vector4f vector = new org.joml.Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        matrix.transform(vector);
        field_34821 = new Vector4f(vector.x, vector.y, vector.z, vector.w);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private boolean isAnyCornerVisible(float x1, float y1, float z1, float x2, float y2, float z2) {
        return jomlFrustum.testAab(x1, y1, z1, x2, y2, z2);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private boolean method_38558(float x1, float y1, float z1, float x2, float y2, float z2) {
        return jomlFrustum.intersectAab(x1, y1, z1, x2, y2, z2) == FrustumIntersection.INSIDE;
    }
}
