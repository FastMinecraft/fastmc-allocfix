package dev.fastmc.allocfix.main.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Mixin(WeightedBakedModel.class)
public class MixinWeightedBakedModel {
    @Shadow
    @Final
    private List<Weighted.Present<BakedModel>> models;

    @Shadow
    @Final
    private int totalWeight;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random)  {
        int weight = Math.abs((int)random.nextLong()) % this.totalWeight;
        for (Weighted.Present<BakedModel> model : models) {
            if ((weight -= model.getWeight().getValue()) >= 0) continue;
            return model.getData().getQuads(state, face, random);
        }
        return Collections.emptyList();
    }
}
