package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedMatrixStack;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
    extends EntityRenderer<T>
    implements FeatureRendererContext<T, M> {
    @Shadow protected abstract boolean isShaking(T entity);

    @Shadow protected abstract float getLyingAngle(T entity);

    @Shadow
    private static float getYaw(Direction direction) {
        return 0;
    }

    protected MixinLivingEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        String string;
        EntityPose entityPose;

        IPatchedMatrixStack patchedMatrixStack = (IPatchedMatrixStack) matrices;

        if (this.isShaking(entity)) {
            bodyYaw += (float)(Math.cos((double) entity.age * 3.25) * Math.PI * (double)0.4f);
        }
        if ((entityPose = entity.getPose()) != EntityPose.SLEEPING) {
            patchedMatrixStack.rotateY(180.0f - bodyYaw);
        }
        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            patchedMatrixStack.rotateZ(f * this.getLyingAngle(entity));
        } else if (entity.isUsingRiptide()) {
            patchedMatrixStack.rotateX(-90.0f - entity.pitch);
            patchedMatrixStack.rotateY(((float) entity.age + tickDelta) * -75.0f);
        } else if (entityPose == EntityPose.SLEEPING) {
            Direction direction = entity.getSleepingDirection();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            patchedMatrixStack.rotateY(g);
            patchedMatrixStack.rotateZ(this.getLyingAngle(entity));
            patchedMatrixStack.rotateY(270.0f);
        } else if ((entity.hasCustomName() || entity instanceof PlayerEntity) && ("Dinnerbone".equals(string = Formatting.strip(
            entity.getName().getString())) || "Grumm".equals(string)) && (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(
            PlayerModelPart.CAPE))) {
            matrices.translate(0.0, entity.getHeight() + 0.1f, 0.0);
            patchedMatrixStack.rotateZ(180.0f);
        }
    }
}
