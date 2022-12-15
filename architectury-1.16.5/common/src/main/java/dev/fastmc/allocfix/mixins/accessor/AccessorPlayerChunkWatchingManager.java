package dev.fastmc.allocfix.mixins.accessor;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.PlayerChunkWatchingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerChunkWatchingManager.class)
public interface AccessorPlayerChunkWatchingManager {
    @Accessor
    Object2BooleanMap<ServerPlayerEntity> getWatchingPlayers();
}
