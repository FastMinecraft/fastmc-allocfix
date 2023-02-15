package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedBiomeEffects;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalInt;

@Mixin(Biome.class)
public abstract class MixinBiome {
    @Shadow
    @Final
    private BiomeEffects effects;

    @Shadow
    protected abstract int getDefaultFoliageColor();

    @Shadow
    protected abstract int getDefaultGrassColor();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public int getGrassColorAt(double x, double z) {
        OptionalInt optional = ((IPatchedBiomeEffects) this.effects).getFoliageColorInt();
        int i = optional.isPresent() ? optional.getAsInt() : this.getDefaultGrassColor();
        return this.effects.getGrassColorModifier().getModifiedGrassColor(x, z, i);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public int getFoliageColor() {
        OptionalInt optional = ((IPatchedBiomeEffects) this.effects).getFoliageColorInt();
        return optional.isPresent() ? optional.getAsInt() : this.getDefaultFoliageColor();
    }
}
