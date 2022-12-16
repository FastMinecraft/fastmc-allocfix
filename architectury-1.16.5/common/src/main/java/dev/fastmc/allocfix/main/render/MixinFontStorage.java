package dev.fastmc.allocfix.main.render;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.font.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {
    @Shadow
    @Final
    private Int2ObjectMap<Glyph> glyphCache;

    @Shadow
    @Final
    private static Glyph SPACE;

    @Shadow
    protected abstract RenderableGlyph getRenderableGlyph(int i);

    @Shadow
    @Final
    private Int2ObjectMap<GlyphRenderer> glyphRendererCache;

    @Shadow
    @Final
    private static EmptyGlyphRenderer EMPTY_GLYPH_RENDERER;

    @Shadow
    protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public Glyph getGlyph(int i) {
        Glyph result = this.glyphCache.get(i);
        if (result == null) {
            result = i == 32 ? SPACE : this.getRenderableGlyph(i);
            this.glyphCache.put(i, result);
        }
        return result;
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public GlyphRenderer getGlyphRenderer(int i) {
        GlyphRenderer result = this.glyphRendererCache.get(i);
        if (result == null) {
            result = i == 32 ? EMPTY_GLYPH_RENDERER : this.getGlyphRenderer(this.getRenderableGlyph(i));
            this.glyphRendererCache.put(i, result);
        }
        return result;
    }
}
