package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.mixins.IPatchedIVertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LightUtil.class, remap = false)
public abstract class MixinLightUtil {
    @Shadow
    public static int[] mapFormats(VertexFormat from, VertexFormat to) {
        throw new AssertionError();
    }

    @Shadow
    public static void unpack(int[] from, float[] to, VertexFormat formatFrom, int v, int e) {
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public static void putBakedQuad(IVertexConsumer consumer, BakedQuad quad) {
        consumer.setTexture(quad.getSprite());
        consumer.setQuadOrientation(quad.getFace());
        if (quad.hasTintIndex()) {consumer.setQuadTint(quad.getTintIndex());}
        consumer.setApplyDiffuseLighting(quad.shouldApplyDiffuseLighting());
        VertexFormat formatFrom = consumer.getVertexFormat();
        VertexFormat formatTo = quad.getFormat();

        float[] data = ((IPatchedIVertexConsumer) consumer).getTempDataArray();
        int countFrom = formatFrom.getElementCount();
        int countTo = formatTo.getElementCount();
        int[] eMap = mapFormats(formatFrom, formatTo);

        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < countFrom; e++) {
                if (eMap[e] != countTo) {
                    unpack(quad.getVertexData(), data, formatTo, v, eMap[e]);
                    consumer.put(e, data);
                } else {
                    consumer.put(e);
                }
            }
        }
    }
}
