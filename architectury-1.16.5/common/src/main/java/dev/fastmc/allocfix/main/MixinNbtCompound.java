package dev.fastmc.allocfix.main;

import dev.fastmc.allocfix.DummyLinkedHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@Mixin(NbtCompound.class)
public class MixinNbtCompound {
    @Redirect(method = "<init>()V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false))
    private static HashMap<?, ?> Redirect$init$INVOKE$Maps$newHashMap() {
        return new DummyLinkedHashMap<>(new Object2ObjectOpenHashMap<>());
    }
    @Redirect(method = "copy()Lnet/minecraft/nbt/NbtCompound;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap(Ljava/util/Map;)Ljava/util/HashMap;", remap = false))
    private static HashMap<?, ?> Redirect$copy$INVOKE$Maps$newHashMap(Map<?, ?> map) {
        return new DummyLinkedHashMap<>(new Object2ObjectOpenHashMap<>(map));
    }
}
