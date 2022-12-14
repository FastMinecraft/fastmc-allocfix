package dev.fastmc.allocfix.main.render;

import dev.fastmc.allocfix.IPatchedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public abstract class MixinParticle implements IPatchedParticle {
    @Shadow
    @Final
    protected ClientWorld world;
    @Shadow
    protected double x;
    @Shadow
    protected double y;
    @Shadow
    protected double z;

    private int brightness = 0;
    private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

    @Inject(method = "tick", at = @At("RETURN"))
    private void Inject$tick$RETURN(CallbackInfo ci) {
        mutablePos.set(x, y, z);
        brightness = WorldRenderer.getLightmapCoordinates(world, mutablePos);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public int getBrightness(float tint) {
        return brightness;
    }

    @Override
    public int getBrightness() {
        return brightness;
    }

    @Override
    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    @NotNull
    @Override
    public World getWorld() {
        return world;
    }

    @NotNull
    @Override
    public BlockPos getBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }
}