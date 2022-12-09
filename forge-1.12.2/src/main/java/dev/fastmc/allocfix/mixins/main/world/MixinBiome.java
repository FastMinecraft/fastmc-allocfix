package dev.fastmc.allocfix.mixins.main.world;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Biome.class)
public class MixinBiome {
    @Shadow @Final private int waterColor;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public int getWaterColorMultiplier() {
        return waterColor;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public int getModdedBiomeGrassColor(int original) {
        return original;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public int getModdedBiomeFoliageColor(int original) {
        return original;
    }
}
