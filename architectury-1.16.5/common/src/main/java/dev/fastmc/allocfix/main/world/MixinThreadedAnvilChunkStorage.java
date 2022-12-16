package dev.fastmc.allocfix.main.world;

import com.mojang.datafixers.DataFixer;
import dev.fastmc.allocfix.accessor.AccessorPlayerChunkWatchingManager;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class MixinThreadedAnvilChunkStorage extends VersionedChunkStorage {
    @Shadow
    @Final
    private ThreadedAnvilChunkStorage.TicketManager ticketManager;

    @Shadow
    @Final
    private PlayerChunkWatchingManager playerChunkWatchingManager;

    @Shadow
    private static double getSquaredDistance(ChunkPos pos, Entity entity) {
        return 0;
    }

    public MixinThreadedAnvilChunkStorage(File file, DataFixer dataFixer, boolean bl) {
        super(file, dataFixer, bl);
    }


    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public boolean isTooFarFromPlayersToSpawnMobs(ChunkPos chunkPos) {
        if (!this.ticketManager.method_20800(chunkPos.toLong())) {
            return true;
        }
        ObjectSet<ServerPlayerEntity> playerWatchingChunk = ((AccessorPlayerChunkWatchingManager) (Object) playerChunkWatchingManager).getWatchingPlayers().keySet();
        for (ServerPlayerEntity serverPlayerEntity : playerWatchingChunk) {
            if (!serverPlayerEntity.isSpectator() && getSquaredDistance(chunkPos, serverPlayerEntity) < 16384.0) {
                return false;
            }
        }
        return true;
    }
}
