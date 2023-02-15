package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.accessor.AccessorFrustum;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public boolean shouldRender(Entity entity, Frustum frustum, double x, double y, double z) {
        if (!entity.shouldRender(x, y, z)) {
            return false;
        } else if (entity.ignoreCameraFrustum) {
            return true;
        } else {
            Box box = entity.getVisibilityBoundingBox();
            double minX = box.minX - 0.5;
            double minY = box.minY - 0.5;
            double minZ = box.minZ - 0.5;
            double maxX = box.maxX + 0.5;
            double maxY = box.maxY + 0.5;
            double maxZ = box.maxZ + 0.5;
            if ((Double.isNaN(minX) || Double.isNaN(minY) || Double.isNaN(minZ)
                || Double.isNaN(maxX) || Double.isNaN(maxY) || Double.isNaN(maxZ))
                || (minX == maxX && minY == maxY && minZ == maxZ)) {
                minX = entity.getX() - 2.0;
                minY = entity.getY() - 2.0;
                minZ = entity.getZ() - 2.0;
                maxX = entity.getX() + 2.0;
                maxY = entity.getY() + 2.0;
                maxZ = entity.getZ() + 2.0;
            }
            return ((AccessorFrustum) frustum).callIsVisible(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}
