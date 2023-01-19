package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedMatrixStack;
import net.minecraft.client.render.entity.*;
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
import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
    extends EntityRenderer<T>
    implements FeatureRendererContext<T, M> {
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow protected abstract boolean isShaking(T entity);

    @Shadow protected abstract float getLyingAngle(T entity);

    @Shadow
    private static float getYaw(Direction direction) {
        return 0;
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
            patchedMatrixStack.rotateY(Math.toRadians(180.0f - bodyYaw));
        }
        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            patchedMatrixStack.rotateZ(Math.toRadians(f * this.getLyingAngle(entity)));
        } else if (entity.isUsingRiptide()) {
            patchedMatrixStack.rotateX(Math.toRadians(-90.0f - entity.getPitch()));
            patchedMatrixStack.rotateY(Math.toRadians(((float) entity.age + tickDelta) * -75.0f));
        } else if (entityPose == EntityPose.SLEEPING) {
            Direction direction = entity.getSleepingDirection();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            patchedMatrixStack.rotateY(Math.toRadians(g));
            patchedMatrixStack.rotateZ(Math.toRadians(this.getLyingAngle(entity)));
            patchedMatrixStack.rotateY(Math.toRadians(270.0f));
        } else if ((entity.hasCustomName() || entity instanceof PlayerEntity) && ("Dinnerbone".equals(string = Formatting.strip(
            entity.getName().getString())) || "Grumm".equals(string)) && (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(
            PlayerModelPart.CAPE))) {
            matrices.translate(0.0, entity.getHeight() + 0.1f, 0.0);
            patchedMatrixStack.rotateZ(Math.toRadians(180.0f));
        }
    }
}
