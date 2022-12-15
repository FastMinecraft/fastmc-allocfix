package dev.fastmc.allocfix.mixins.main;

import dev.fastmc.allocfix.DummyLinkedHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.util.ClassInheritanceMultiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Set;

@Mixin(ClassInheritanceMultiMap.class)
public class MixinClassInheritanceMultiMap {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false))
    private HashMap<?, ?> Redirect$init$INVOKE$Maps$newHashMap() {
        return new DummyLinkedHashMap<>(new Object2ObjectOpenHashMap<>());
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newIdentityHashSet()Ljava/util/Set;", remap = false))
    private Set<?> Redirect$init$INVOKE$Sets$newIdentityHashSet() {
        return new ReferenceOpenHashSet<>();
    }
}
