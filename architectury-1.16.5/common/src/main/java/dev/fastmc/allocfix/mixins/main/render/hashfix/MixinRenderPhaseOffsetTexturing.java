package dev.fastmc.allocfix.mixins.main.render.hashfix;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ RenderPhase.OffsetTexturing.class })
public class MixinRenderPhaseOffsetTexturing {
    @Shadow
    @Final
    private float x;
    @Shadow
    @Final
    private float y;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public int hashCode() {
        return Float.hashCode(this.x) * 31 + Float.hashCode(this.y);
    }
}
