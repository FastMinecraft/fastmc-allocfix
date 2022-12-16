package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedIBlockAccess;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockFluidRenderer.class)
public class MixinBlockFluidRenderer {
    @Shadow
    @Final
    private TextureAtlasSprite[] atlasSpritesWater;

    @Shadow
    @Final
    private TextureAtlasSprite[] atlasSpritesLava;

    @Shadow
    @Final
    private BlockColors blockColors;

    @Shadow
    private TextureAtlasSprite atlasSpriteWaterOverlay;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public boolean renderFluid(
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos pos,
        BufferBuilder bufferBuilder
    ) {
        IPatchedIBlockAccess patched = (IPatchedIBlockAccess) blockAccess;

        BlockLiquid blockLiquid = (BlockLiquid) blockState.getBlock();
        boolean isLava = blockState.getMaterial() == Material.LAVA;
        TextureAtlasSprite[] sprites = isLava ? this.atlasSpritesLava : this.atlasSpritesWater;

        int color = this.blockColors.colorMultiplier(blockState, blockAccess, pos, 0);
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        boolean renderUp = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.UP);
        boolean renderDown = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.DOWN);
        
        boolean renderN = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.NORTH);
        boolean renderS = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.SOUTH);
        boolean renderW = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.WEST);
        boolean renderE = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.EAST);

        if (!renderUp && !renderDown && !renderN && !renderS && !renderW && !renderE) {
            return false;
        } else {
            Material material = blockState.getMaterial();

            int blockX = pos.getX();
            int blockY = pos.getY();
            int blockZ = pos.getZ();

            float height = this.getFluidHeight(patched, blockX, blockY, blockZ, material);
            float heightS = this.getFluidHeight(patched, blockX, blockY, blockZ + 1, material);
            float heightES = this.getFluidHeight(patched, blockX + 1, blockY, blockZ + 1, material);
            float heightE = this.getFluidHeight(patched, blockX + 1, blockY, blockZ, material);

            boolean rendered = false;
            
            if (renderUp) {
                rendered = true;
                float f12 = BlockLiquid.getSlopeAngle(blockAccess, pos, material, blockState);
                TextureAtlasSprite textureatlassprite = f12 > -999.0F ? sprites[1] : sprites[0];
                
                height -= 0.001F;
                heightS -= 0.001F;
                heightES -= 0.001F;
                heightE -= 0.001F;
                
                float u1;
                float u2;
                float u3;
                float u4;
                float v1;
                float v2;
                float v3;
                float v4;

                if (f12 < -999.0F) {
                    u1 = textureatlassprite.getInterpolatedU(0.0D);
                    v1 = textureatlassprite.getInterpolatedV(0.0D);
                    u2 = u1;
                    v2 = textureatlassprite.getInterpolatedV(16.0D);
                    u3 = textureatlassprite.getInterpolatedU(16.0D);
                    v3 = v2;
                    u4 = u3;
                    v4 = v1;
                } else {
                    float f21 = MathHelper.sin(f12) * 0.25F;
                    float f22 = MathHelper.cos(f12) * 0.25F;
                    u1 = textureatlassprite.getInterpolatedU((8.0F + (-f22 - f21) * 16.0F));
                    v1 = textureatlassprite.getInterpolatedV((8.0F + (-f22 + f21) * 16.0F));
                    u2 = textureatlassprite.getInterpolatedU((8.0F + (-f22 + f21) * 16.0F));
                    v2 = textureatlassprite.getInterpolatedV((8.0F + (f22 + f21) * 16.0F));
                    u3 = textureatlassprite.getInterpolatedU((8.0F + (f22 + f21) * 16.0F));
                    v3 = textureatlassprite.getInterpolatedV((8.0F + (f22 - f21) * 16.0F));
                    u4 = textureatlassprite.getInterpolatedU((8.0F + (f22 - f21) * 16.0F));
                    v4 = textureatlassprite.getInterpolatedV((8.0F + (-f22 - f21) * 16.0F));
                }

                int k2 = blockState.getPackedLightmapCoords(blockAccess, pos);
                int l2 = k2 >> 16 & 65535;
                int i3 = k2 & 65535;

                quad(
                    bufferBuilder,
                    (double) blockX + 0.0D,
                    (double) blockX + 0.0D,
                    (double) blockX + 1.0D,
                    (double) blockX + 1.0D,
                    (double) blockY + height,
                    (double) blockY + heightS,
                    (double) blockY + heightES,
                    (double) blockY + heightE,
                    (double) blockZ + 0.0D,
                    (double) blockZ + 1.0D,
                    u1,
                    u2,
                    u3,
                    u4,
                    v1,
                    v2,
                    v3,
                    v4,
                    red,
                    green,
                    blue,
                    l2,
                    i3
                );

                if (blockLiquid.shouldRenderSides(blockAccess, pos.up())) {
                    quad(
                        bufferBuilder,
                        (double) blockX + 0.0D,
                        (double) blockX + 1.0D,
                        (double) blockX + 1.0D,
                        (double) blockX + 0.0D,
                        (double) blockY + height,
                        (double) blockY + heightE,
                        (double) blockY + heightES,
                        (double) blockY + heightS,
                        (double) blockZ + 0.0D,
                        (double) blockZ + 0.0D,
                        u1,
                        u4,
                        u3,
                        u2,
                        v1,
                        v4,
                        v3,
                        v2,
                        red,
                        green,
                        blue,
                        l2,
                        i3
                    );
                }
            }

            if (renderDown) {
                float downU1 = sprites[0].getMinU();
                float f36 = sprites[0].getMaxU();
                float f37 = sprites[0].getMinV();
                float f38 = sprites[0].getMaxV();
                int l1 = blockState.getPackedLightmapCoords(blockAccess, pos.down());
                int i2 = l1 >> 16 & 65535;
                int j2 = l1 & 65535;
                quad(
                    bufferBuilder,
                    blockX,
                    blockX,
                    (double) blockX + 1.0D,
                    (double) blockX + 1.0D,
                    blockY,
                    blockY,
                    blockY,
                    blockY,
                    (double) blockZ + 1.0D,
                    blockZ,
                    downU1,
                    downU1,
                    f36,
                    f36,
                    f38,
                    f37,
                    f37,
                    f38,
                    0.5F,
                    0.5F,
                    0.5F,
                    i2,
                    j2
                );
                rendered = true;
            }

            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int i = 0; i < 4; ++i) {
                int xOffset = 0;
                int zOffset = 0;
                
                boolean renderCorner = false;

                switch (i) {
                    case 0:
                        --zOffset;
                        renderCorner = renderN;
                        break;
                    case 1:
                        ++zOffset;
                        renderCorner = renderN;
                        break;
                    case 2:
                        --xOffset;
                        renderCorner = renderN;
                        break;
                    case 3:
                        ++xOffset;
                        renderCorner = renderN;
                        break;
                }
                

                mutablePos.setPos(blockX + xOffset, blockY, blockZ + zOffset);
                TextureAtlasSprite sprite = sprites[1];

                if (!isLava) {
                    IBlockState state = blockAccess.getBlockState(mutablePos);

                    if (state.getBlockFaceShape(
                        blockAccess,
                        mutablePos,
                        EnumFacing.VALUES[i + 2].getOpposite()
                    ) == BlockFaceShape.SOLID) {
                        sprite = this.atlasSpriteWaterOverlay;
                    }
                }

                if (renderCorner) {
                    float yOffset1;
                    float yOffset2;
                    double d3;
                    double d4;
                    double d5;
                    double d6;

                    if (i == 0) {
                        yOffset1 = height;
                        yOffset2 = heightE;
                        d3 = blockX;
                        d5 = (double) blockX + 1.0D;
                        d4 = (double) blockZ + 0.0010000000474974513D;
                        d6 = (double) blockZ + 0.0010000000474974513D;
                    } else if (i == 1) {
                        yOffset1 = heightES;
                        yOffset2 = heightS;
                        d3 = (double) blockX + 1.0D;
                        d5 = blockX;
                        d4 = (double) blockZ + 1.0D - 0.0010000000474974513D;
                        d6 = (double) blockZ + 1.0D - 0.0010000000474974513D;
                    } else if (i == 2) {
                        yOffset1 = heightS;
                        yOffset2 = height;
                        d3 = (double) blockX + 0.0010000000474974513D;
                        d5 = (double) blockX + 0.0010000000474974513D;
                        d4 = (double) blockZ + 1.0D;
                        d6 = blockZ;
                    } else {
                        yOffset1 = heightE;
                        yOffset2 = heightES;
                        d3 = (double) blockX + 1.0D - 0.0010000000474974513D;
                        d5 = (double) blockX + 1.0D - 0.0010000000474974513D;
                        d4 = blockZ;
                        d6 = (double) blockZ + 1.0D;
                    }

                    rendered = true;
                    float u1 = sprite.getInterpolatedU(0.0D);
                    float u2 = sprite.getInterpolatedU(8.0D);
                    float f28 = sprite.getInterpolatedV(((1.0F - yOffset1) * 16.0F * 0.5F));
                    float f29 = sprite.getInterpolatedV(((1.0F - yOffset2) * 16.0F * 0.5F));
                    float f30 = sprite.getInterpolatedV(8.0D);
                    int j = blockState.getPackedLightmapCoords(blockAccess, mutablePos);
                    int k = j >> 16 & 65535;
                    int l = j & 65535;
                    float f31 = i < 2 ? 0.8F : 0.6F;
                    float f32 = 1.0F * f31 * red;
                    float f33 = 1.0F * f31 * green;
                    float f34 = 1.0F * f31 * blue;
                    quad(
                        bufferBuilder,
                        d3,
                        d5,
                        d5,
                        d3,
                        (double) blockY + yOffset1,
                        (double) blockY + yOffset2,
                        (double) blockY + 0.0D,
                        (double) blockY + 0.0D,
                        d4,
                        d6,
                        u1,
                        u2,
                        u2,
                        u1,
                        f28,
                        f29,
                        f30,
                        f30,
                        f32,
                        f33,
                        f34,
                        k,
                        l
                    );

                    if (sprite != this.atlasSpriteWaterOverlay) {
                        quad(
                            bufferBuilder,
                            d3,
                            d5,
                            d5,
                            d3,
                            (double) blockY + 0.0D,
                            (double) blockY + 0.0D,
                            (double) blockY + yOffset2,
                            (double) blockY + yOffset1,
                            d4,
                            d6,
                            u1,
                            u2,
                            u2,
                            u1,
                            f30,
                            f30,
                            f29,
                            f28,
                            f32,
                            f33,
                            f34,
                            k,
                            l
                        );
                    }
                }
            }

            return rendered;
        }
    }

    private static void quad(
        BufferBuilder bufferBuilder,
        double x1,
        double x2,
        double x3,
        double x4,
        double y1,
        double y2,
        double y3,
        double y4,
        double z1,
        double z2,
        float u1,
        float u2,
        float u3,
        float u4,
        float v1,
        float v2,
        float v3,
        float v4,
        float red,
        float green,
        float blue,
        int skyLight,
        int blockLight
    ) {
        bufferBuilder.pos(x1, y1, z1)
            .color(red, green, blue, 1.0F)
            .tex(u1, v1)
            .lightmap(skyLight, blockLight)
            .endVertex();

        bufferBuilder.pos(x2, y2, z2)
            .color(red, green, blue, 1.0F)
            .tex(u2, v2)
            .lightmap(skyLight, blockLight)
            .endVertex();

        bufferBuilder.pos(x3, y3, z2)
            .color(red, green, blue, 1.0F)
            .tex(u3, v3)
            .lightmap(skyLight, blockLight)
            .endVertex();

        bufferBuilder.pos(x4, y4, z1)
            .color(red, green, blue, 1.0F)
            .tex(u4, v4)
            .lightmap(skyLight, blockLight)
            .endVertex();
    }


    private float getFluidHeight(IPatchedIBlockAccess patched, int x, int y, int z, Material blockMaterial) {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < 4; ++j) {
            int x1 = x - (j & 1);
            int z1 = z - (j >> 1 & 1);
            if (patched.getBlockState(x1, y + 1, z1).getMaterial() == blockMaterial) {
                return 1.0f;
            }

            IBlockState state = patched.getBlockState(x1, y, z1);
            Material material = state.getMaterial();

            if (material != blockMaterial) {
                if (!material.isSolid()) {
                    ++f;
                    ++i;
                }
            } else {
                int k = state.getValue(BlockLiquid.LEVEL);
                if (k >= 8 || k == 0) {
                    f += BlockLiquid.getLiquidHeightPercent(k) * 10.0f;
                    i += 10;
                }
                f += BlockLiquid.getLiquidHeightPercent(k);
                ++i;
            }
        }

        return 1.0F - f / (float) i;
    }
}
