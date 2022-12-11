package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.util.AdaptersKt;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;
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
    private void init(Matrix4f matrix4f, Matrix4f matrix4f2) {
        org.joml.Matrix4f projection = AdaptersKt.toJoml(matrix4f);
        org.joml.Matrix4f viewModel = AdaptersKt.toJoml(matrix4f2);
        jomlFrustum.set(projection.mul(viewModel), false);
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
