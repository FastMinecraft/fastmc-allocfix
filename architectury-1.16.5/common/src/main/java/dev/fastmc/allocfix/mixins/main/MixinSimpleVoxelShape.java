package dev.fastmc.allocfix.mixins.main;

import dev.fastmc.allocfix.mixins.IPatchedVoxelShape;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleVoxelShape.class)
public class MixinSimpleVoxelShape extends VoxelShape implements IPatchedVoxelShape {
    public MixinSimpleVoxelShape(VoxelSet voxelSet) {
        super(voxelSet);
    }

    private static final int CLASS_HASH = SimpleVoxelShape.class.hashCode();
    private int hash = 0;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init$Inject$RETURN(VoxelSet voxelSet, CallbackInfo ci) {
        hash = CLASS_HASH;
        hash = 31 * hash + voxelSet.getXSize();
        hash = 31 * hash + voxelSet.getYSize();
        hash = 31 * hash + voxelSet.getZSize();
    }

    @Override
    public int hash() {
        return hash;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public DoubleList getPointPositions(Direction.Axis axis) {
        return IPatchedVoxelShape.getFractionalDoubleList(this.voxels.getSize(axis));
    }
}
