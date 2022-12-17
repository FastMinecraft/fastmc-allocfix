package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedVisGraph;
import dev.fastmc.common.collection.IntArrayFIFOQueueNoShrink;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;

@Mixin(VisGraph.class)
public abstract class MixinVisGraph implements IPatchedVisGraph {
    @Shadow
    @Final
    private BitSet bitSet;

    @Shadow
    protected abstract void addEdges(int pos, Set<EnumFacing> p_178610_2_);

    @Shadow
    protected abstract int getNeighborIndexAtFace(int pos, EnumFacing facing);

    @Shadow private int empty;
    private static final EnumFacing[] FACINGS = EnumFacing.values();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    private Set<EnumFacing> floodFill(int pos) {
        Set<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
        IntArrayFIFOQueueNoShrink queue = new IntArrayFIFOQueueNoShrink(300);
        queue.enqueue(pos);

        this.bitSet.set(pos, true);

        while (!queue.isEmpty()) {
            int i = queue.dequeueInt();
            this.addEdges(i, set);

            for (EnumFacing enumfacing : FACINGS) {
                int j = this.getNeighborIndexAtFace(i, enumfacing);

                if (j >= 0 && !this.bitSet.get(j)) {
                    this.bitSet.set(j, true);
                    queue.enqueue(j);
                }
            }
        }

        return set;
    }

    @Override
    public void setOpaqueCube(int x, int y, int z) {
        this.bitSet.set(x | y << 8 | z << 4, true);
        --this.empty;
    }
}
