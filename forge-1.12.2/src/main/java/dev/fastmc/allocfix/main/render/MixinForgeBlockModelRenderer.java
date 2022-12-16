package dev.fastmc.allocfix.main.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = ForgeBlockModelRenderer.class, remap = false)
public class MixinForgeBlockModelRenderer {

    private static final EnumFacing[] FACINGS = EnumFacing.values();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Overwrite
    public static boolean render(
        VertexLighterFlat lighter,
        IBlockAccess world,
        IBakedModel model,
        IBlockState state,
        BlockPos pos,
        BufferBuilder wr,
        boolean checkSides,
        long rand
    ) {
        lighter.setWorld(world);
        lighter.setState(state);
        lighter.setBlockPos(pos);
        boolean empty = true;
        List<BakedQuad> quads = model.getQuads(state, null, rand);

        if (!quads.isEmpty()) {
            lighter.updateBlockInfo();
            empty = false;
            for (int i = 0, quadsSize = quads.size(); i < quadsSize; i++) {
                BakedQuad quad = quads.get(i);
                quad.pipe(lighter);
            }
        }

        for (int i = 0, facingsLength = FACINGS.length; i < facingsLength; i++) {
            EnumFacing side = FACINGS[i];
            List<BakedQuad> facingQuad = model.getQuads(state, side, rand);
            if (facingQuad.isEmpty()) continue;
            if (checkSides && !state.shouldSideBeRendered(world, pos, side)) continue;

            if (empty) {
                lighter.updateBlockInfo();
            }

            empty = false;
            for (int j = 0, quadsSize = facingQuad.size(); j < quadsSize; j++) {
                BakedQuad quad = facingQuad.get(j);
                quad.pipe(lighter);
            }
        }

        lighter.resetBlockInfo();

        return !empty;
    }
}
