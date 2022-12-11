package dev.fastmc.allocfix.mixins.accessor;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface AccessorThreadedAnvilChunkStorage {
    @Invoker
    Iterable<ChunkHolder> callEntryIterator();
}
