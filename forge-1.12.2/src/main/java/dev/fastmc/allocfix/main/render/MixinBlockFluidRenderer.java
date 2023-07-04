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
import org.spongepowered.asm.mixin.Unique;

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

        boolean renderD = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.DOWN);
        boolean renderU = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.UP);
        boolean renderN = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.NORTH);
        boolean renderS = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.SOUTH);
        boolean renderW = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.WEST);
        boolean renderE = blockState.shouldSideBeRendered(blockAccess, pos, EnumFacing.EAST);

        if (!renderD && !renderU && !renderN && !renderS && !renderW && !renderE) return false;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        IPatchedIBlockAccess patched = (IPatchedIBlockAccess) blockAccess;

        BlockLiquid blockLiquid = (BlockLiquid) blockState.getBlock();
        Material material = blockState.getMaterial();

        boolean isLava = material == Material.LAVA;
        TextureAtlasSprite[] sprites = isLava ? this.atlasSpritesLava : this.atlasSpritesWater;

        int color = this.blockColors.colorMultiplier(blockState, blockAccess, pos, 0);
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        float heightNW = this.fastmc_allocfix$getFluidHeight(patched, blockX, blockY, blockZ, material);
        float heightSW = this.fastmc_allocfix$getFluidHeight(patched, blockX, blockY, blockZ + 1, material);
        float heightNE = this.fastmc_allocfix$getFluidHeight(patched, blockX + 1, blockY, blockZ, material);
        float heightSE = this.fastmc_allocfix$getFluidHeight(patched, blockX + 1, blockY, blockZ + 1, material);

        if (renderU) {
            float flowAngle = BlockLiquid.getSlopeAngle(blockAccess, pos, material, blockState);

            heightNW -= 0.001;
            heightSW -= 0.001;
            heightSE -= 0.001;
            heightNE -= 0.001;

            float uNW;
            float uSW;
            float uSE;
            float uNE;

            float vNW;
            float vSW;
            float vSE;
            float vNE;

            if (flowAngle < -999.0f) {
                TextureAtlasSprite sprite = sprites[0];

                uNW = sprite.getInterpolatedU(0.0);
                uSW = uNW;
                uSE = sprite.getInterpolatedU(16.0);
                uNE = uSE;

                vNW = sprite.getInterpolatedV(0.0);
                vNE = vNW;
                vSW = sprite.getInterpolatedV(16.0);
                vSE = vSW;
            } else {
                TextureAtlasSprite sprite = sprites[1];
                float flowX = MathHelper.sin(flowAngle) * 0.25f;
                float flowZ = MathHelper.cos(flowAngle) * 0.25f;

                uNW = sprite.getInterpolatedU((8.0f + (-flowZ - flowX) * 16.0f));
                uSW = sprite.getInterpolatedU((8.0f + (-flowZ + flowX) * 16.0f));
                uSE = sprite.getInterpolatedU((8.0f + (flowZ + flowX) * 16.0f));
                uNE = sprite.getInterpolatedU((8.0f + (flowZ - flowX) * 16.0f));

                vNW = sprite.getInterpolatedV((8.0f + (-flowZ + flowX) * 16.0f));
                vSW = sprite.getInterpolatedV((8.0f + (flowZ + flowX) * 16.0f));
                vSE = sprite.getInterpolatedV((8.0f + (flowZ - flowX) * 16.0f));
                vNE = sprite.getInterpolatedV((8.0f + (-flowZ - flowX) * 16.0f));
            }

            int lightUp = blockState.getPackedLightmapCoords(blockAccess, pos);

            fastmc_allocfix$quad(
                bufferBuilder,
                blockX + 0.0,
                blockX + 0.0,
                blockX + 1.0,
                blockX + 1.0,
                blockY + heightNW,
                blockY + heightSW,
                blockY + heightSE,
                blockY + heightNE,
                blockZ + 0.0,
                blockZ + 1.0,
                blockZ + 1.0,
                blockZ + 0.0,
                uNW,
                uSW,
                uSE,
                uNE,
                vNW,
                vSW,
                vSE,
                vNE,
                red,
                green,
                blue,
                lightUp
            );

            if (blockLiquid.shouldRenderSides(blockAccess, mutablePos.setPos(pos).move(EnumFacing.UP))) {
                fastmc_allocfix$quad(
                    bufferBuilder,
                    blockX + 0.0,
                    blockX + 1.0,
                    blockX + 1.0,
                    blockX + 0.0,
                    blockY + heightNW,
                    blockY + heightNE,
                    blockY + heightSE,
                    blockY + heightSW,
                    blockZ + 0.0,
                    blockZ + 0.0,
                    blockZ + 1.0,
                    blockZ + 1.0,
                    uNW,
                    uNE,
                    uSE,
                    uSW,
                    vNW,
                    vNE,
                    vSE,
                    vSW,
                    red,
                    green,
                    blue,
                    lightUp
                );
            }
        }

        if (renderD) {
            float u1 = sprites[0].getMinU();
            float u2 = sprites[0].getMaxU();
            float v1 = sprites[0].getMinV();
            float v2 = sprites[0].getMaxV();
            int lightDown = blockState.getPackedLightmapCoords(blockAccess, mutablePos.setPos(pos).move(EnumFacing.DOWN));

            fastmc_allocfix$quad(
                bufferBuilder,
                blockX,
                blockX,
                blockX + 1.0,
                blockX + 1.0,
                blockY,
                blockY,
                blockY,
                blockY,
                blockZ + 1.0,
                blockZ,
                blockZ,
                blockZ + 1.0,
                u1,
                u1,
                u2,
                u2,
                v2,
                v1,
                v1,
                v2,
                0.5f,
                0.5f,
                0.5f,
                lightDown
            );
        }

        for (int i = 0; i < 4; ++i) {
            boolean renderSide;

            switch (i) {
                case 0:
                    renderSide = renderN;
                    break;
                case 1:
                    renderSide = renderS;
                    break;
                case 2:
                    renderSide = renderW;
                    break;
                default:
                    renderSide = renderE;
                    break;
            }

            if (!renderSide) continue;

            EnumFacing direction;

            float y11;
            float y12;
            double x1;
            double z1;
            double x2;
            double z2;

            switch (i) {
                case 0:
                    direction = EnumFacing.NORTH;

                    y11 = heightNW;
                    y12 = heightNE;

                    x1 = blockX + 0.0;
                    x2 = blockX + 1.0;
                    z1 = blockZ + 0.001;
                    z2 = blockZ + 0.001;
                    break;
                case 1:
                    direction = EnumFacing.SOUTH;

                    y11 = heightSE;
                    y12 = heightSW;

                    x1 = blockX + 1.0;
                    x2 = blockX + 0.0;
                    z1 = blockZ + 1.0 - 0.001;
                    z2 = blockZ + 1.0 - 0.001;
                    break;
                case 2:
                    direction = EnumFacing.WEST;

                    y11 = heightSW;
                    y12 = heightNW;

                    x1 = blockX + 0.001;
                    x2 = blockX + 0.001;
                    z1 = blockZ + 1.0;
                    z2 = blockZ + 0.0;
                    break;
                default:
                    direction = EnumFacing.EAST;

                    y11 = heightNE;
                    y12 = heightSE;

                    x1 = blockX + 1.0 - 0.001;
                    x2 = blockX + 1.0 - 0.001;
                    z1 = blockZ + 0.0;
                    z2 = blockZ + 1.0;
                    break;
            }

            TextureAtlasSprite sprite = sprites[1];
            mutablePos.setPos(pos).move(direction);

            if (!isLava) {
                IBlockState offsetState = blockAccess.getBlockState(mutablePos);

                if (offsetState.getBlockFaceShape(blockAccess, mutablePos, EnumFacing.VALUES[i + 2].getOpposite())
                    == BlockFaceShape.SOLID) {
                    sprite = this.atlasSpriteWaterOverlay;
                }
            }

            float uN = sprite.getInterpolatedU(0.0);
            float uS = sprite.getInterpolatedU(8.0);

            float vFrom1 = sprite.getInterpolatedV((1.0f - y11) * 16.0f * 0.5f);
            float vFrom2 = sprite.getInterpolatedV((1.0f - y12) * 16.0f * 0.5f);
            float vTo = sprite.getInterpolatedV(8.0);

            float shade = i < 2 ? 0.8f : 0.6f;
            float sideR = shade * red;
            float sideG = shade * green;
            float sideB = shade * blue;

            int lightSide = blockState.getPackedLightmapCoords(blockAccess, mutablePos);

            fastmc_allocfix$quad(
                bufferBuilder,
                x1,
                x2,
                x2,
                x1,
                blockY + y11,
                blockY + y12,
                blockY,
                blockY,
                z1,
                z2,
                z2,
                z1,
                uN,
                uS,
                uS,
                uN,
                vFrom1,
                vFrom2,
                vTo,
                vTo,
                sideR,
                sideG,
                sideB,
                lightSide
            );

            if (sprite != this.atlasSpriteWaterOverlay) {
                fastmc_allocfix$quad(
                    bufferBuilder,
                    x1,
                    x2,
                    x2,
                    x1,
                    blockY,
                    blockY,
                    blockY + y12,
                    blockY + y11,
                    z1,
                    z2,
                    z2,
                    z1,
                    uN,
                    uS,
                    uS,
                    uN,
                    vTo,
                    vTo,
                    vFrom2,
                    vFrom1,
                    sideR,
                    sideG,
                    sideB,
                    lightSide
                );
            }
        }

        return true;
    }

    @Unique
    private static void fastmc_allocfix$quad(
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
        double z3,
        double z4,
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
        int light
    ) {
        int skyLight = light >> 16 & 0xFFFF;
        int blockLight = light & 0xFFFF;

        bufferBuilder.pos(x1, y1, z1)
            .color(red, green, blue, 1.0f)
            .tex(u1, v1)
            .lightmap(skyLight, blockLight)
            .endVertex();

        bufferBuilder.pos(x2, y2, z2)
            .color(red, green, blue, 1.0f)
            .tex(u2, v2)
            .lightmap(skyLight, blockLight)
            .endVertex();

        bufferBuilder.pos(x3, y3, z3)
            .color(red, green, blue, 1.0f)
            .tex(u3, v3)
            .lightmap(skyLight, blockLight)
            .endVertex();

        bufferBuilder.pos(x4, y4, z4)
            .color(red, green, blue, 1.0f)
            .tex(u4, v4)
            .lightmap(skyLight, blockLight)
            .endVertex();
    }


    @Unique
    private float fastmc_allocfix$getFluidHeight(
        IPatchedIBlockAccess patched,
        int x,
        int y,
        int z,
        Material blockMaterial
    ) {
        int i = 0;
        float f = 0.0f;

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

        return 1.0f - f / (float) i;
    }
}
