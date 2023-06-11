package dev.fastmc.allocfix;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import org.jetbrains.annotations.NotNull;

public class LightGatheringFastMc extends QuadGatheringTransformer {
    private static final VertexFormat FORMAT = new VertexFormat()
        .addElement(DefaultVertexFormats.TEX_2F)
        .addElement(DefaultVertexFormats.TEX_2S);

    public int blockLight, skyLight;

    {setVertexFormat(FORMAT);}

    public boolean hasLighting() {
        return dataLength[1] >= 2;
    }

    @Override
    protected void processQuad() {
        // Reset light data
        blockLight = 0;
        skyLight = 0;
        // Compute average light for all 4 vertices
        for (int i = 0; i < 4; i++) {
            blockLight += (int) ((quadData[1][i][0] * 0xFFFF) / 0x20);
            skyLight += (int) ((quadData[1][i][1] * 0xFFFF) / 0x20);
        }
        // Values must be multiplied by 16, divided by 4 for average => x4
        blockLight *= 4;
        skyLight *= 4;
    }

    // Dummy overrides

    @Override
    public void setQuadTint(int tint) {}

    @Override
    public void setQuadOrientation(@NotNull EnumFacing orientation) {}

    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {}

    @Override
    public void setTexture(@NotNull TextureAtlasSprite texture) {}
}