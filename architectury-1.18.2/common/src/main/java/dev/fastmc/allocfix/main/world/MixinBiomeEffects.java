package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedBiomeEffects;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(BiomeEffects.class)
public class MixinBiomeEffects implements IPatchedBiomeEffects {

    private OptionalInt grassColorInt;
    private OptionalInt foliageColorInt;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$RETURN(
        int fogColor,
        int waterColor,
        int waterFogColor,
        int skyColor,
        Optional<Integer> foliageColor,
        Optional<Integer> grassColor,
        BiomeEffects.GrassColorModifier grassColorModifier,
        Optional<BiomeParticleConfig> particleConfig,
        Optional<SoundEvent> loopSound,
        Optional<BiomeMoodSound> moodSound,
        Optional<BiomeAdditionsSound> additionsSound,
        Optional<MusicSound> music,
        CallbackInfo ci
    ) {
        grassColorInt = grassColor.map(OptionalInt::of).orElseGet(OptionalInt::empty);
        foliageColorInt = foliageColor.map(OptionalInt::of).orElseGet(OptionalInt::empty);
    }

    @NotNull
    @Override
    public OptionalInt getGrassColorInt() {
        return grassColorInt;
    }

    @NotNull
    @Override
    public OptionalInt getFoliageColorInt() {
        return foliageColorInt;
    }
}
