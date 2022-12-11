package dev.fastmc.allocfix.mixins.main;

import dev.fastmc.allocfix.mixins.IPatchedVoxelShape;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SimpleVoxelShape.class)
public class MixinSimpleVoxelShape extends VoxelShape implements IPatchedVoxelShape {
    public MixinSimpleVoxelShape(VoxelSet voxelSet) {
        super(voxelSet);
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
