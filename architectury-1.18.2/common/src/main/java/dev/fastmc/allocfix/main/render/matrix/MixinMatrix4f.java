package dev.fastmc.allocfix.main.render.matrix;

import dev.fastmc.allocfix.IMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public class MixinMatrix4f implements IMatrix4f {
    @Shadow
    public float a00;
    @Shadow
    public float a01;
    @Shadow
    public float a02;
    @Shadow
    public float a03;
    @Shadow
    public float a10;
    @Shadow
    public float a11;
    @Shadow
    public float a12;
    @Shadow
    public float a13;
    @Shadow
    public float a20;
    @Shadow
    public float a21;
    @Shadow
    public float a22;
    @Shadow
    public float a23;
    @Shadow
    public float a30;
    @Shadow
    public float a31;
    @Shadow
    public float a32;
    @Shadow
    public float a33;

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
    public float m03() {
        return a30;
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
    public float m13() {
        return a31;
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
    public float m23() {
        return a32;
    }

    @Override
    public float m30() {
        return a03;
    }

    @Override
    public float m31() {
        return a13;
    }

    @Override
    public float m32() {
        return a23;
    }

    @Override
    public float m33() {
        return a33;
    }


    @Override
    public IMatrix4f m00(float m00) {
        a00 = m00;
        return this;
    }

    @Override
    public IMatrix4f m01(float m01) {
        a10 = m01;
        return this;
    }

    @Override
    public IMatrix4f m02(float m02) {
        a20 = m02;
        return this;
    }

    @Override
    public IMatrix4f m03(float m03) {
        a30 = m03;
        return this;
    }

    @Override
    public IMatrix4f m10(float m10) {
        a01 = m10;
        return this;
    }

    @Override
    public IMatrix4f m11(float m11) {
        a11 = m11;
        return this;
    }

    @Override
    public IMatrix4f m12(float m12) {
        a21 = m12;
        return this;
    }

    @Override
    public IMatrix4f m13(float m13) {
        a31 = m13;
        return this;
    }

    @Override
    public IMatrix4f m20(float m20) {
        a02 = m20;
        return this;
    }

    @Override
    public IMatrix4f m21(float m21) {
        a12 = m21;
        return this;
    }

    @Override
    public IMatrix4f m22(float m22) {
        a22 = m22;
        return this;
    }

    @Override
    public IMatrix4f m23(float m23) {
        a32 = m23;
        return this;
    }

    @Override
    public IMatrix4f m30(float m30) {
        a03 = m30;
        return this;
    }

    @Override
    public IMatrix4f m31(float m31) {
        a13 = m31;
        return this;
    }

    @Override
    public IMatrix4f m32(float m32) {
        a23 = m32;
        return this;
    }

    @Override
    public IMatrix4f m33(float m33) {
        a33 = m33;
        return this;
    }
}
