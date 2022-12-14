package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.AllocationCounter;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public class MixinGuiOverlayDebug {
    @Inject(method = "getDebugInfoRight", at = @At("RETURN"))
    private void Inject$getDebugInfoRight$RETURN(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().set(3, AllocationCounter.INSTANCE.getRenderText());
    }
}
