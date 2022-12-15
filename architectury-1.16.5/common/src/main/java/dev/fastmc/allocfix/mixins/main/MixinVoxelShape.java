package dev.fastmc.allocfix.mixins.main;

import dev.fastmc.allocfix.mixins.IPatchedVoxelShape;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VoxelShape.class)
public class MixinVoxelShape implements IPatchedVoxelShape {

}
