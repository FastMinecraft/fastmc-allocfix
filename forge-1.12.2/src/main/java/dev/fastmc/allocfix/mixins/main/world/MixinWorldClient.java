package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.DummyLinkedHashSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;

@Mixin(WorldClient.class)
public class MixinWorldClient {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;", remap = false), expect = 4)
    private HashSet<?> Redirect$init$INVOKE$Sets$newHashSet() {
        return new DummyLinkedHashSet<>(new ObjectLinkedOpenHashSet<>());
    }
}
