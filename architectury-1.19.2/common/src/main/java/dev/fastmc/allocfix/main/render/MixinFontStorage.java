package dev.fastmc.allocfix.main.render;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {
    @Shadow
    @Final
    private Int2ObjectMap<FontStorage.GlyphPair> glyphCache;

    @Shadow
    @Final
    private Int2ObjectMap<GlyphRenderer> glyphRendererCache;

    @Shadow
    protected abstract FontStorage.GlyphPair findGlyph(int par1);

    @Shadow
    protected abstract GlyphRenderer findGlyphRenderer(int par1);

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public Glyph getGlyph(int codePoint, boolean validateAdvance) {
        FontStorage.GlyphPair result = this.glyphCache.get(codePoint);
        if (result == null) {
            result = findGlyph(codePoint);
            this.glyphCache.put(codePoint, result);
        }
        return result.getGlyph(validateAdvance);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public GlyphRenderer getGlyphRenderer(int codePoint) {
        GlyphRenderer result = this.glyphRendererCache.get(codePoint);
        if (result == null) {
            result = findGlyphRenderer(codePoint);
            this.glyphRendererCache.put(codePoint, result);
        }
        return result;
    }
}
