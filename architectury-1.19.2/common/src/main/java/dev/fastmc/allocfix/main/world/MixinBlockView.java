package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedBlockView;
import dev.fastmc.allocfix.IPatchedBlockView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockView.class)
public interface MixinBlockView extends IPatchedBlockView {
}
