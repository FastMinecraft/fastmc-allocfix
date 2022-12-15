package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.IPatchedBlockView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockView.class)
public interface MixinBlockView extends IPatchedBlockView {
}
