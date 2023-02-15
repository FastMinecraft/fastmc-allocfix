package dev.fastmc.allocfix.accessor;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.Projection.class)
public interface AccessorProjection {
    @Accessor
    Vec3d getCenter();

    @Accessor("x")
    Vec3d getX();

    @Accessor("y")
    Vec3d getY();
}
