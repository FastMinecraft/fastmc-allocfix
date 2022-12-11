package dev.fastmc.allocfix.mixins.accessor;

import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Frustum.class)
public interface AccessorFrustum {
    @Invoker
    boolean callIsAnyCornerVisible(float x1, float y1, float z1, float x2, float y2, float z2);

    @Invoker
    boolean callIsVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
}
