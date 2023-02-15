package dev.fastmc.allocfix.main;

import dev.fastmc.allocfix.AllocationCounter;
import dev.fastmc.allocfix.FastMcAllocFixMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> {
    @Shadow
    @Final
    public GameOptions options;

    public MixinMinecraftClient(String string) {
        super(string);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void Inject$runGameLoop$HEAD(CallbackInfo ci) {
        if (this.options.debugEnabled) {
            AllocationCounter.INSTANCE.update();
        } else {
            AllocationCounter.INSTANCE.reset();
        }
    }

    static {
        FastMcAllocFixMod.INSTANCE.preInit();
    }
}
