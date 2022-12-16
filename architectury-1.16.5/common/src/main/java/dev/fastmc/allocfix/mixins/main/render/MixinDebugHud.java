package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.AllocationCounter;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {
    @Inject(method = "getRightText", at = @At("RETURN"))
    private void Inject$getRightText$RETURN(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().set(3, AllocationCounter.INSTANCE.getRenderText());
    }


}
