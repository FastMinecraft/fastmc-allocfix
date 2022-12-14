package dev.fastmc.allocfix.main.world;

import com.google.common.base.Predicate;
import dev.fastmc.allocfix.IPatchedIBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(WorldGenMinable.class)
public class MixinWorldGenMinable {

    @Shadow
    @Final
    private int numberOfBlocks;

    @Shadow
    @Final
    private Predicate<IBlockState> predicate;

    @Shadow
    @Final
    private IBlockState oreBlock;


    public boolean generate(World worldIn, Random rand, BlockPos position) {

        float f = rand.nextFloat() * (float) Math.PI;
        double d0 = (float) (position.getX() + 8) + MathHelper.sin(f) * (float) this.numberOfBlocks / 8.0F;
        double d1 = (float) (position.getX() + 8) - MathHelper.sin(f) * (float) this.numberOfBlocks / 8.0F;
        double d2 = (float) (position.getZ() + 8) + MathHelper.cos(f) * (float) this.numberOfBlocks / 8.0F;
        double d3 = (float) (position.getZ() + 8) - MathHelper.cos(f) * (float) this.numberOfBlocks / 8.0F;
        double d4 = position.getY() + rand.nextInt(3) - 2;
        double d5 = position.getY() + rand.nextInt(3) - 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i < this.numberOfBlocks; ++i) {
            float f1 = (float) i / (float) this.numberOfBlocks;
            double d6 = d0 + (d1 - d0) * (double) f1;
            double d7 = d4 + (d5 - d4) * (double) f1;
            double d8 = d2 + (d3 - d2) * (double) f1;
            double d9 = rand.nextDouble() * (double) this.numberOfBlocks / 16.0D;
            double d10 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int l1 = j; l1 <= i1; ++l1) {
                double d12 = ((double) l1 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        double d13 = ((double) i2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int j2 = l; j2 <= k1; ++j2) {
                                double d14 = ((double) j2 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    mutable.setPos(l1, i2, j2);

                                    IBlockState state = worldIn.getBlockState(mutable);
                                    if (state.getBlock().isReplaceableOreGen(
                                        state,
                                        worldIn,
                                        mutable,
                                        this.predicate
                                    )) {
                                        worldIn.setBlockState(mutable, this.oreBlock, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
