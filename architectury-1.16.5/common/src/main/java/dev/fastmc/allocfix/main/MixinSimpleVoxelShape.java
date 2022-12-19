package dev.fastmc.allocfix.main;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleVoxelShape.class)
public class MixinSimpleVoxelShape extends VoxelShape {
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
    public int hashCode() {
        return hash;
    }

    private static final FractionalDoubleList[] CACHE = new FractionalDoubleList[2048];

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public DoubleList getPointPositions(Direction.Axis axis) {
        int sectionCount = this.voxels.getSize(axis);
        if (sectionCount >= 2048) {
            return new FractionalDoubleList(sectionCount);
        }

        FractionalDoubleList value = CACHE[sectionCount];
        if (value == null) {
            value = new FractionalDoubleList(sectionCount);
            CACHE[sectionCount] = value;
        }

        return value;
    }
}
