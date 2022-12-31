package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {
    @Inject(method = "tickParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;tick()V", shift = At.Shift.AFTER))
    private void tickParticle$Inject$INVOKE$tick$AFTER(Particle particle, CallbackInfo ci) {
        ((IPatchedParticle) particle).updateBrightness();
    }
}