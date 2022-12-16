package dev.fastmc.allocfix.mixins.main.render;

import dev.fastmc.allocfix.IMatrix3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("DuplicatedCode")
@Mixin(Matrix3f.class)
public class MixinMatrix3f implements IMatrix3f {
    @Shadow
    public float a00;
    @Shadow
    public float a01;
    @Shadow
    public float a02;
    @Shadow
    public float a10;
    @Shadow
    public float a11;
    @Shadow
    public float a12;
    @Shadow
    public float a20;
    @Shadow
    public float a21;
    @Shadow
    public float a22;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    @Environment(value= EnvType.CLIENT)
    public void multiply(Quaternion quaternion) {
        this.rotateQuaternion(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW());
    }

    @Override
    public float m00() {
        return a00;
    }

    @Override
    public float m01() {
        return a10;
    }

    @Override
    public float m02() {
        return a20;
    }

    @Override
    public float m10() {
        return a01;
    }

    @Override
    public float m11() {
        return a11;
    }

    @Override
    public float m12() {
        return a21;
    }

    @Override
    public float m20() {
        return a02;
    }

    @Override
    public float m21() {
        return a12;
    }

    @Override
    public float m22() {
        return a22;
    }


    @Override
    public IMatrix3f m00(float m00) {
        a00 = m00;
        return this;
    }

    @Override
    public IMatrix3f m01(float m01) {
        a10 = m01;
        return this;
    }

    @Override
    public IMatrix3f m02(float m02) {
        a20 = m02;
        return this;
    }

    @Override
    public IMatrix3f m10(float m10) {
        a01 = m10;
        return this;
    }

    @Override
    public IMatrix3f m11(float m11) {
        a11 = m11;
        return this;
    }

    @Override
    public IMatrix3f m12(float m12) {
        a21 = m12;
        return this;
    }

    @Override
    public IMatrix3f m20(float m20) {
        a02 = m20;
        return this;
    }

    @Override
    public IMatrix3f m21(float m21) {
        a12 = m21;
        return this;
    }

    @Override
    public IMatrix3f m22(float m22) {
        a22 = m22;
        return this;
    }
}
