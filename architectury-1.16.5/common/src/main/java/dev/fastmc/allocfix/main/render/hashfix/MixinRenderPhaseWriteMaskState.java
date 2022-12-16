package dev.fastmc.allocfix.main.render.hashfix;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ RenderPhase.WriteMaskState.class })
public class MixinRenderPhaseWriteMaskState {
    @Shadow
    @Final
    private boolean color;
    @Shadow
    @Final
    private boolean depth;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public int hashCode() {
        return Boolean.hashCode(this.color) * 31 + Boolean.hashCode(this.depth);
    }
}
