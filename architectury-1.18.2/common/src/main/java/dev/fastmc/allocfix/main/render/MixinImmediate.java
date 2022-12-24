package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.DummyLinkedHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;

@Mixin(VertexConsumerProvider.Immediate.class)
public class MixinImmediate {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;", remap = false))
    private HashSet<?> Redirect$init$INVOKE$Sets$newHashSet() {
        return new DummyLinkedHashSet<>(new ObjectOpenHashSet<>());
    }
}
