package dev.fastmc.allocfix.main;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.ArrayVoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ArrayVoxelShape.class)
public class MixinArrayVoxelShape {
    @Shadow
    @Final
    private DoubleList xPoints;
    @Shadow
    @Final
    private DoubleList yPoints;
    @Shadow
    @Final
    private DoubleList zPoints;

    private static final int CLASS_HASH = ArrayVoxelShape.class.hashCode();

    private boolean hashInitialized = false;
    private int hash = 0;

    @Override
    public int hashCode() {
        if (!hashInitialized) {
            int hash = CLASS_HASH;
            hash = 31 * hash + xPoints.hashCode();
            hash = 31 * hash + yPoints.hashCode();
            hash = 31 * hash + zPoints.hashCode();
            this.hash = hash;
            hashInitialized = true;
        }
        return hash;
    }
}
