package dev.fastmc.allocfix.main.world.dimensionfx;

import dev.fastmc.allocfix.IPatchedDimensionEffects;
import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DimensionEffects.Nether.class)
public class MixinNether implements IPatchedDimensionEffects {
    @Override
    public int adjustFogColor(int color, float sunHeight) {
        return color;
    }
}
