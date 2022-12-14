package dev.fastmc.allocfix.mixins.main;

import dev.fastmc.allocfix.AllocationCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow public GameSettings gameSettings;

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void Inject$runGameLoop$HEAD(CallbackInfo ci) {
        if (this.gameSettings.showDebugInfo) {
            AllocationCounter.INSTANCE.update();
        } else {
            AllocationCounter.INSTANCE.reset();
        }
    }
}
