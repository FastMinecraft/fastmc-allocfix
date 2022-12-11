package dev.fastmc.allocfix.mixins.main;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> {
    public MixinMinecraftClient(String string) {
        super(string);
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void Inject$run$HEAD(CallbackInfo ci) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;yield()V", remap = false))
    private void render$Redirect$INVOKE$yield() {}
}
