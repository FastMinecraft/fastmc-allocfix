package dev.fastmc.allocfix.mixins.main.render.hashfix;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalDouble;

@Mixin({ RenderPhase.LineWidth.class })
public class MixinRenderPhaseLineWidth {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    @Final
    private OptionalDouble width;

    private boolean hashCalculated = false;
    private int hash = 0;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public int hashCode() {
        if (!hashCalculated) {
            hash = super.hashCode() * 31 + width.hashCode();
            hashCalculated = true;
        }
        return hash;
    }
}
