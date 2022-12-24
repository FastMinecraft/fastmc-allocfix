package dev.fastmc.allocfix.main.world.dimensionfx;

import dev.fastmc.allocfix.IPatchedDimensionEffects;
import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DimensionEffects.class)
public class MixinDimensionEffects implements IPatchedDimensionEffects {}
