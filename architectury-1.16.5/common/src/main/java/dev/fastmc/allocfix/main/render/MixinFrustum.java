package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.util.Adapters;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Matrix4f;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Frustum.class)
public class MixinFrustum {
    private final FrustumIntersection jomlFrustum = new FrustumIntersection();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private void init(Matrix4f modelViewIn, Matrix4f projectionIn) {
        org.joml.Matrix4f projection = Adapters.toJoml(projectionIn);
        org.joml.Matrix4f modelView = Adapters.toJoml(modelViewIn);
        jomlFrustum.set(projection.mul(modelView), false);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private boolean isAnyCornerVisible(float x1, float y1, float z1, float x2, float y2, float z2) {
        return jomlFrustum.testAab(x1, y1, z1, x2, y2, z2);
    }
}
