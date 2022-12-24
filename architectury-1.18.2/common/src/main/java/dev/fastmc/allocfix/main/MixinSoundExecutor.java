package dev.fastmc.allocfix.main;

import net.minecraft.client.sound.SoundExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundExecutor.class)
public class MixinSoundExecutor {
    @Inject(method = "waitForStop", at = @At("HEAD"))
    private void Inject$waitForStop$HEAD(CallbackInfo ci) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }
}
