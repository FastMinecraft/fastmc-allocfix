package dev.fastmc.allocfix.mixins.main.render;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = VertexBufferConsumer.class, remap = false)
public class MixinVertexBufferConsumer {
    @Shadow
    private BlockPos offset;

    private BlockPos.MutableBlockPos mutableBlockPos;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void setOffset(BlockPos offset) {
        if (mutableBlockPos == null) {
            mutableBlockPos = new BlockPos.MutableBlockPos();
            this.offset = mutableBlockPos;
        }
        mutableBlockPos.setPos(offset);
    }
}
