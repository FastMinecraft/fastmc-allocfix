package dev.fastmc.allocfix.main.world.dimensionfx;

import dev.fastmc.allocfix.IPatchedDimensionEffects;
import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DimensionEffects.Overworld.class)
public class MixinOverworld implements IPatchedDimensionEffects {
    @Override
    public int adjustFogColor(int color, float sunHeight) {
        int r = (int) ((color >> 20 & 0x3FF) * (sunHeight * 0.94f + 0.06f));
        int g = (int) ((color >> 10 & 0x3FF) * (sunHeight * 0.94f + 0.06f));
        int b = (int) ((color & 0x3FF) * (sunHeight * 0.91f + 0.09f));
        return r << 20 | g << 10 | b;
    }
}
