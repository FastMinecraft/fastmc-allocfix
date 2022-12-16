package dev.fastmc.allocfix.main;

import dev.fastmc.allocfix.IPatchedVoxelShape;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VoxelShape.class)
public class MixinVoxelShape implements IPatchedVoxelShape {

}
