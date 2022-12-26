package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.accessor.AccessorProjection;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow
    private boolean ready;
    @Shadow
    @Final
    private BlockPos.Mutable blockPos;
    @Shadow
    private Vec3d pos;
    @Shadow
    private BlockView area;

    @Shadow
    public abstract Camera.Projection getProjection();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public CameraSubmersionType getSubmersionType() {
        if (!this.ready) {
            return CameraSubmersionType.NONE;
        }
        FluidState fluidState = this.area.getFluidState(this.blockPos);
        if (fluidState.isIn(FluidTags.WATER) && this.pos.y < this.blockPos.getY() + fluidState.getHeight(
            this.area,
            this.blockPos
        )) {
            return CameraSubmersionType.WATER;
        }

        AccessorProjection projection = (AccessorProjection) this.getProjection();
        Vec3d center = projection.getCenter();
        Vec3d projectionX = projection.getX();
        Vec3d projectionY = projection.getY();
        CameraSubmersionType result;

        double bottomRightX = this.pos.x + center.x + projectionY.x + projectionX.x;
        double bottomRightY = this.pos.y + center.y + projectionY.y + projectionX.y;
        double bottomRightZ = this.pos.z + center.z + projectionY.z + projectionX.z;
        result = check(bottomRightX, bottomRightY, bottomRightZ);
        if (result != null) return result;

        double topRightX = this.pos.x + center.x + projectionY.x - projectionX.x;
        double topRightY = this.pos.y + center.y + projectionY.y - projectionX.y;
        double topRightZ = this.pos.z + center.z + projectionY.z - projectionX.z;
        result = check(topRightX, topRightY, topRightZ);
        if (result != null) return result;

        double bottomLeftX = this.pos.x + center.x - projectionY.x + projectionX.x;
        double bottomLeftY = this.pos.y + center.y - projectionY.y + projectionX.y;
        double bottomLeftZ = this.pos.z + center.z - projectionY.z + projectionX.z;
        result = check(bottomLeftX, bottomLeftY, bottomLeftZ);
        if (result != null) return result;

        double topLeftX = this.pos.x + center.x - projectionY.x - projectionX.x;
        double topLeftY = this.pos.y + center.y - projectionY.y - projectionX.y;
        double topLeftZ = this.pos.z + center.z - projectionY.z - projectionX.z;
        result = check(topLeftX, topLeftY, topLeftZ);
        if (result != null) return result;

        return CameraSubmersionType.NONE;
    }

    private static final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

    private CameraSubmersionType check(double x, double y, double z) {
        mutablePos.set(x, y, z);
        FluidState fluidState2 = this.area.getFluidState(mutablePos);
        if (fluidState2.isIn(FluidTags.LAVA)) {
            if (y <= fluidState2.getHeight(this.area, mutablePos) + mutablePos.getY()) {
                return CameraSubmersionType.LAVA;
            }
            return null;
        }
        BlockState blockState = this.area.getBlockState(mutablePos);
        if (!blockState.isOf(Blocks.POWDER_SNOW)) return null;
        return CameraSubmersionType.POWDER_SNOW;
    }
}
