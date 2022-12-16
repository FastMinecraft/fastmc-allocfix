/*
 * Adapted from org.joml.Matrix3f
 *
 * The MIT() License
 *
 * Copyright (c) 2015-2021 Richard Greenlees
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, mod()ify, mer()ge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MER()CHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.fastmc.allocfix;

import org.joml.Math;

public interface IMatrix3f {
    float m00();

    float m01();

    float m02();

    float m10();

    float m11();

    float m12();

    float m20();

    float m21();

    float m22();


    IMatrix3f m00(float m00);

    IMatrix3f m01(float m01);

    IMatrix3f m02(float m02);

    IMatrix3f m10(float m10);

    IMatrix3f m11(float m11);

    IMatrix3f m12(float m12);

    IMatrix3f m20(float m20);

    IMatrix3f m21(float m21);

    IMatrix3f m22(float m22);


    default IMatrix3f set(IMatrix3f other) {
        return m00(other.m00())
            .m01(other.m01())
            .m02(other.m02())
            .m10(other.m10())
            .m11(other.m11())
            .m12(other.m12())
            .m20(other.m20())
            .m21(other.m21())
            .m22(other.m22());
    }

    default IMatrix3f mul(IMatrix3f right) {
        return mul(right, this);
    }

    default IMatrix3f mul(IMatrix3f right, IMatrix3f dest) {
        float nm00 = Math.fma(m00(), right.m00(), Math.fma(m10(), right.m01(), m20() * right.m02()));
        float nm01 = Math.fma(m01(), right.m00(), Math.fma(m11(), right.m01(), m21() * right.m02()));
        float nm02 = Math.fma(m02(), right.m00(), Math.fma(m12(), right.m01(), m22() * right.m02()));
        float nm10 = Math.fma(m00(), right.m10(), Math.fma(m10(), right.m11(), m20() * right.m12()));
        float nm11 = Math.fma(m01(), right.m10(), Math.fma(m11(), right.m11(), m21() * right.m12()));
        float nm12 = Math.fma(m02(), right.m10(), Math.fma(m12(), right.m11(), m22() * right.m12()));
        float nm20 = Math.fma(m00(), right.m20(), Math.fma(m10(), right.m21(), m20() * right.m22()));
        float nm21 = Math.fma(m01(), right.m20(), Math.fma(m11(), right.m21(), m21() * right.m22()));
        float nm22 = Math.fma(m02(), right.m20(), Math.fma(m12(), right.m21(), m22() * right.m22()));
        return dest.m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m10(nm10)
            .m11(nm11)
            .m12(nm12)
            .m20(nm20)
            .m21(nm21)
            .m22(nm22);
    }

    default IMatrix3f rotateQuaternion(float x, float y, float z, float w) {
        return rotateQuaternion(x, y, z, w, this);
    }

    default IMatrix3f rotateQuaternion(float x, float y, float z, float w, IMatrix3f dest) {
        float w2 = w * w, x2 = x * x;
        float y2 = y * y, z2 = z * z;

        float zw = z * w, dzw = zw + zw, xy = x * y, dxy = xy + xy;
        float xz = x * z, dxz = xz + xz, yw = y * w, dyw = yw + yw;
        float yz = y * z, dyz = yz + yz, xw = x * w, dxw = xw + xw;

        float rm00 = w2 + x2 - z2 - y2;
        float rm01 = dxy + dzw;
        float rm02 = dxz - dyw;

        float rm10 = dxy - dzw;
        float rm11 = y2 - z2 + w2 - x2;
        float rm12 = dyz + dxw;

        float rm20 = dyw + dxz;
        float rm21 = dyz - dxw;
        float rm22 = z2 - y2 - x2 + w2;

        float nm00 = m00() * rm00 + m10() * rm01 + m20() * rm02;
        float nm01 = m01() * rm00 + m11() * rm01 + m21() * rm02;
        float nm02 = m02() * rm00 + m12() * rm01 + m22() * rm02;
        float nm10 = m00() * rm10 + m10() * rm11 + m20() * rm12;
        float nm11 = m01() * rm10 + m11() * rm11 + m21() * rm12;
        float nm12 = m02() * rm10 + m12() * rm11 + m22() * rm12;

        return dest.m20(m00() * rm20 + m10() * rm21 + m20() * rm22)
            .m21(m01() * rm20 + m11() * rm21 + m21() * rm22)
            .m22(m02() * rm20 + m12() * rm21 + m22() * rm22)
            .m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m10(nm10)
            .m11(nm11)
            .m12(nm12);
    }

    default IMatrix3f scale(float x, float y, float z) {
        return scale(x, y, z, this);
    }

    default IMatrix3f scale(float x, float y, float z, IMatrix3f dest) {
        return dest.m00(m00() * x)
            .m01(m01() * x)
            .m02(m02() * x)
            .m10(m10() * y)
            .m11(m11() * y)
            .m12(m12() * y)
            .m20(m20() * z)
            .m21(m21() * z)
            .m22(m22() * z);
    }

    default IMatrix3f rotate(float ang, float x, float y, float z) {
        return rotate(ang, x, y, z, this);
    }

    default IMatrix3f rotate(float ang, float x, float y, float z, IMatrix3f dest) {
        float s = Math.sin(ang);
        float c = Math.cosFromSin(s, ang);
        float C = 1.0f - c;

        // rotation matrix elements:
        // m30, m31, m32, m03, m13, m23 = 0
        float xx = x * x, xy = x * y, xz = x * z;
        float yy = y * y, yz = y * z;
        float zz = z * z;

        float rm00 = xx * C + c;
        float rm01 = xy * C + z * s;
        float rm02 = xz * C - y * s;

        float rm10 = xy * C - z * s;
        float rm11 = yy * C + c;
        float rm12 = yz * C + x * s;

        float rm20 = xz * C + y * s;
        float rm21 = yz * C - x * s;
        float rm22 = zz * C + c;

        // add temporaries for dependent values
        float nm00 = m00() * rm00 + m10() * rm01 + m20() * rm02;
        float nm01 = m01() * rm00 + m11() * rm01 + m21() * rm02;
        float nm02 = m02() * rm00 + m12() * rm01 + m22() * rm02;
        float nm10 = m00() * rm10 + m10() * rm11 + m20() * rm12;
        float nm11 = m01() * rm10 + m11() * rm11 + m21() * rm12;
        float nm12 = m02() * rm10 + m12() * rm11 + m22() * rm12;

        // set non-dependent values directly
        return dest.m20(m00() * rm20 + m10() * rm21 + m20() * rm22)
            .m21(m01() * rm20 + m11() * rm21 + m21() * rm22)
            .m22(m02() * rm20 + m12() * rm21 + m22() * rm22)
            // set other values
            .m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m10(nm10)
            .m11(nm11)
            .m12(nm12);
    }
}
