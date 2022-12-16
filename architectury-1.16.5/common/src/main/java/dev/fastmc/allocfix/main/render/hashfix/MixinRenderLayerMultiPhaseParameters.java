package dev.fastmc.allocfix.main.render.hashfix;

import com.google.common.collect.ImmutableList;
import dev.fastmc.allocfix.IPatchedMultiPhaseParameters;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public class MixinRenderLayerMultiPhaseParameters implements IPatchedMultiPhaseParameters {
    @Shadow
    @Final
    private RenderLayer.OutlineMode outlineMode;

    private boolean hashCalculated = false;
    private int hash = 0;

    @Redirect(method = "<init>*", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;", remap = false))
    private ImmutableList<?> Redirect$init$INVOKE$ImmutableList(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7, Object e8, Object e9, Object e10, Object e11, Object e12, Object... others) {
        return ImmutableList.of();
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        RenderLayer.MultiPhaseParameters otherCasted = (RenderLayer.MultiPhaseParameters)object;
        return this.outlineMode == otherCasted.outlineMode && this.eq(otherCasted);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public int hashCode() {
        if (!hashCalculated) {
            hash = this.hash() * 31 + outlineMode.hashCode();
            hashCalculated = true;
        }
        return hash;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite(remap = false)
    public String toString() {
        return "CompositeState[" + this.asString() + ", outlineProperty=" + this.outlineMode + ']';
    }
}
