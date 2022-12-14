package dev.fastmc.allocfix.mixins.main;

import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Direction.class)
public class MixinDirection {

    @Shadow
    @Final
    private int idOpposite;

    @Shadow
    @Final
    private int id;

    private static Direction[] valuesOverride;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void Inject$init$HEAD(CallbackInfo ci) {
        if (valuesOverride == null) {
            valuesOverride = new Direction[6];
        }
        valuesOverride[this.id] = (Direction) (Object) this;
    }

    /**
     * @author Luna
     * @reason Optimization
     */
    @Overwrite
    public Direction getOpposite() {
        return valuesOverride[this.idOpposite];
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static Direction[] values() {
        return valuesOverride;
    }
}
