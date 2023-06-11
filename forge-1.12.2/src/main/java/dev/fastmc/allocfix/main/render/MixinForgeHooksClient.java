package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.accessor.AccessorLightGatheringTransformer;
import dev.fastmc.common.collection.FastObjectArrayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ForgeHooksClient.class, remap = false)
public abstract class MixinForgeHooksClient {


    @Shadow
    @Final
    private static IVertexConsumer lightGatherer;

    @Shadow
    private static void drawSegment(
        RenderItem ri,
        int baseColor,
        ItemStack stack,
        List<BakedQuad> segment,
        int bl,
        int sl,
        boolean shade,
        boolean updateLighting,
        boolean updateShading
    ) {
    }

    private static final FastObjectArrayList<BakedQuad> QUADS_LIST = new FastObjectArrayList<>();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static void renderLitItem(RenderItem ri, IBakedModel model, int color, ItemStack stack) {
        FastObjectArrayList<BakedQuad> quadsList = QUADS_LIST;
        quadsList.clearFast();

        for (EnumFacing enumfacing : EnumFacing.VALUES) {
            quadsList.addAll(model.getQuads(null, enumfacing, 0));
        }

        quadsList.addAll(model.getQuads(null, null, 0));

        if (quadsList.isEmpty()) return;

        // Current list of consecutive quads with the same lighting
        List<BakedQuad> segment = new ArrayList<>();

        // Lighting of the current segment
        int segmentBlockLight = 0;
        int segmentSkyLight = 0;
        // Diffuse lighting state
        boolean segmentShading = true;
        // State changed by the current segment
        boolean segmentLightingDirty = false;
        boolean segmentShadingDirty = false;
        // If the current segment contains lighting data
        boolean hasLighting = false;

        for (int i = 0; i < quadsList.size(); i++) {
            BakedQuad q = quadsList.get(i);

            // Lighting of the current quad
            int bl = 0;
            int sl = 0;

            // Fail-fast on ITEM, as it cannot have light data
            if (q.getFormat() != DefaultVertexFormats.ITEM && q.getFormat().hasUvOffset(1)) {
                AccessorLightGatheringTransformer lightGathererAccessor = (AccessorLightGatheringTransformer) MixinForgeHooksClient.lightGatherer;
                q.pipe(lightGatherer);
                if (lightGathererAccessor.callHasLighting()) {
                    bl = lightGathererAccessor.getBlockLight();
                    sl = lightGathererAccessor.getSkyLight();
                }
            }

            boolean shade = q.shouldApplyDiffuseLighting();

            boolean lightingDirty = segmentBlockLight != bl || segmentSkyLight != sl;
            boolean shadeDirty = shade != segmentShading;

            // If lighting or color data has changed, draw the segment and flush it
            if (lightingDirty || shadeDirty) {
                if (i > 0) // Make sure this isn't the first quad being processed
                {
                    drawSegment(
                        ri,
                        color,
                        stack,
                        segment,
                        segmentBlockLight,
                        segmentSkyLight,
                        segmentShading,
                        segmentLightingDirty && (hasLighting || segment.size() < i),
                        segmentShadingDirty
                    );
                }
                segmentBlockLight = bl;
                segmentSkyLight = sl;
                segmentShading = shade;
                segmentLightingDirty = lightingDirty;
                segmentShadingDirty = shadeDirty;
                hasLighting = segmentBlockLight > 0 || segmentSkyLight > 0 || !segmentShading;
            }

            segment.add(q);
        }

        drawSegment(
            ri,
            color,
            stack,
            segment,
            segmentBlockLight,
            segmentSkyLight,
            segmentShading,
            segmentLightingDirty && (hasLighting || segment.size() < quadsList.size()),
            segmentShadingDirty
        );

        // Clean up render state if necessary
        if (hasLighting) {
            OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit,
                OpenGlHelper.lastBrightnessX,
                OpenGlHelper.lastBrightnessY
            );
            GlStateManager.enableLighting();
        }
    }
}
