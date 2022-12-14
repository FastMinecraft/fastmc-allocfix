package dev.fastmc.allocfix.mixins.main;

import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnumFacing.class)
public class MixinEnumFacing {

    @Shadow @Final private int index;
    private static EnumFacing[] valuesOverride;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$HEAD(CallbackInfo ci) {
        if (valuesOverride == null) {
            valuesOverride = new EnumFacing[6];
        }
        valuesOverride[this.index] = (EnumFacing) (Object) this;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static EnumFacing[] values() {
        return valuesOverride;
    }
}
