/*
 * Adapted from org.joml.Matrix4f
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

public interface IMatrix4f {
    float m00();
    float m01();
    float m02();
    float m03();

    float m10();
    float m11();
    float m12();
    float m13();

    float m20();
    float m21();
    float m22();
    float m23();

    float m30();
    float m31();
    float m32();
    float m33();


    IMatrix4f m00(float m00);
    IMatrix4f m01(float m01);
    IMatrix4f m02(float m02);
    IMatrix4f m03(float m03);

    IMatrix4f m10(float m10);
    IMatrix4f m11(float m11);
    IMatrix4f m12(float m12);
    IMatrix4f m13(float m13);

    IMatrix4f m20(float m20);
    IMatrix4f m21(float m21);
    IMatrix4f m22(float m22);
    IMatrix4f m23(float m23);

    IMatrix4f m30(float m30);
    IMatrix4f m31(float m31);
    IMatrix4f m32(float m32);
    IMatrix4f m33(float m33);


    default IMatrix4f set(IMatrix4f other) {
        return m00(other.m00())
            .m01(other.m01())
            .m02(other.m02())
            .m03(other.m03())
            .m10(other.m10())
            .m11(other.m11())
            .m12(other.m12())
            .m13(other.m13())
            .m20(other.m20())
            .m21(other.m21())
            .m22(other.m22())
            .m23(other.m23())
            .m30(other.m30())
            .m31(other.m31())
            .m32(other.m32())
            .m33(other.m33());
    }
    
    default IMatrix4f translate(float x, float y, float z, IMatrix4f dest) {
        return dest.m00(m00())
            .m01(m01())
            .m02(m02())
            .m03(m03())
            .m10(m10())
            .m11(m11())
            .m12(m12())
            .m13(m13())
            .m20(m20())
            .m21(m21())
            .m22(m22())
            .m23(m23())
            .m30(Math.fma(m00(), x, Math.fma(m10(), y, Math.fma(m20(), z, m30()))))
            .m31(Math.fma(m01(), x, Math.fma(m11(), y, Math.fma(m21(), z, m31()))))
            .m32(Math.fma(m02(), x, Math.fma(m12(), y, Math.fma(m22(), z, m32()))))
            .m33(Math.fma(m03(), x, Math.fma(m13(), y, Math.fma(m23(), z, m33()))));
    }

    default IMatrix4f translate(float x, float y, float z) {
        return m30(Math.fma(m00(), x, Math.fma(m10(), y, Math.fma(m20(), z, m30()))))
            .m31(Math.fma(m01(), x, Math.fma(m11(), y, Math.fma(m21(), z, m31()))))
            .m32(Math.fma(m02(), x, Math.fma(m12(), y, Math.fma(m22(), z, m32()))))
            .m33(Math.fma(m03(), x, Math.fma(m13(), y, Math.fma(m23(), z, m33()))));
    }

    default IMatrix4f mul(IMatrix4f right) {
        return mul(right, this);
    }

    default IMatrix4f mul(IMatrix4f right, IMatrix4f dest) {
        float nm00 = Math.fma(m00(), right.m00(), Math.fma(m10(), right.m01(), Math.fma(m20(), right.m02(), m30() * right.m03())));
        float nm01 = Math.fma(m01(), right.m00(), Math.fma(m11(), right.m01(), Math.fma(m21(), right.m02(), m31() * right.m03())));
        float nm02 = Math.fma(m02(), right.m00(), Math.fma(m12(), right.m01(), Math.fma(m22(), right.m02(), m32() * right.m03())));
        float nm03 = Math.fma(m03(), right.m00(), Math.fma(m13(), right.m01(), Math.fma(m23(), right.m02(), m33() * right.m03())));

        float nm10 = Math.fma(m00(), right.m10(), Math.fma(m10(), right.m11(), Math.fma(m20(), right.m12(), m30() * right.m13())));
        float nm11 = Math.fma(m01(), right.m10(), Math.fma(m11(), right.m11(), Math.fma(m21(), right.m12(), m31() * right.m13())));
        float nm12 = Math.fma(m02(), right.m10(), Math.fma(m12(), right.m11(), Math.fma(m22(), right.m12(), m32() * right.m13())));
        float nm13 = Math.fma(m03(), right.m10(), Math.fma(m13(), right.m11(), Math.fma(m23(), right.m12(), m33() * right.m13())));

        float nm20 = Math.fma(m00(), right.m20(), Math.fma(m10(), right.m21(), Math.fma(m20(), right.m22(), m30() * right.m23())));
        float nm21 = Math.fma(m01(), right.m20(), Math.fma(m11(), right.m21(), Math.fma(m21(), right.m22(), m31() * right.m23())));
        float nm22 = Math.fma(m02(), right.m20(), Math.fma(m12(), right.m21(), Math.fma(m22(), right.m22(), m32() * right.m23())));
        float nm23 = Math.fma(m03(), right.m20(), Math.fma(m13(), right.m21(), Math.fma(m23(), right.m22(), m33() * right.m23())));

        float nm30 = Math.fma(m00(), right.m30(), Math.fma(m10(), right.m31(), Math.fma(m20(), right.m32(), m30() * right.m33())));
        float nm31 = Math.fma(m01(), right.m30(), Math.fma(m11(), right.m31(), Math.fma(m21(), right.m32(), m31() * right.m33())));
        float nm32 = Math.fma(m02(), right.m30(), Math.fma(m12(), right.m31(), Math.fma(m22(), right.m32(), m32() * right.m33())));
        float nm33 = Math.fma(m03(), right.m30(), Math.fma(m13(), right.m31(), Math.fma(m23(), right.m32(), m33() * right.m33())));

       return dest.m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m03(nm03)
            .m10(nm10)
            .m11(nm11)
            .m12(nm12)
            .m13(nm13)
            .m20(nm20)
            .m21(nm21)
            .m22(nm22)
            .m23(nm23)
            .m30(nm30)
            .m31(nm31)
            .m32(nm32)
            .m33(nm33);
    }

    default void scale(float x, float y, float z) {
        m00(m00() * x)
            .m01(m01() * x)
            .m02(m02() * x)
            .m03(m03() * x)
            .m10(m10() * y)
            .m11(m11() * y)
            .m12(m12() * y)
            .m13(m13() * y)
            .m20(m20() * z)
            .m21(m21() * z)
            .m22(m22() * z)
            .m23(m23() * z)
            .m30(m30())
            .m31(m31())
            .m32(m32())
            .m33(m33());
    }

    default IMatrix4f rotateQuaternion(float x, float y, float z, float w) {
        return rotateQuaternion(x, y, z, w, this);
    }
    
    default IMatrix4f rotateQuaternion(float x, float y, float z, float w, IMatrix4f dest) {
        float w2 = w * w, x2 = x * x;
        float y2 = y * y, z2 = z * z;
        float zw = z * w, dzw = zw + zw, xy = x * y, dxy = xy + xy;
        float xz = x * z, dxz = xz + xz, yw = y * w, dyw = yw + yw;
        float yz = y * z, dyz = yz + yz, xw = x * w, dxw = xw + xw;

        float rm00 = w2 + x2 - z2 - y2;
        float rm01 = dxy + dzw;
        float rm02 = dxz - dyw;

        float rm10 = -dzw + dxy;
        float rm11 = y2 - z2 + w2 - x2;
        float rm12 = dyz + dxw;

        float rm20 = dyw + dxz;
        float rm21 = dyz - dxw;
        float rm22 = z2 - y2 - x2 + w2;

        float nm00 = m00() * rm00 + m10() * rm01 + m20() * rm02;
        float nm01 = m01() * rm00 + m11() * rm01 + m21() * rm02;
        float nm02 = m02() * rm00 + m12() * rm01 + m22() * rm02;
        float nm03 = m03() * rm00 + m13() * rm01 + m23() * rm02;

        float nm10 = m00() * rm10 + m10() * rm11 + m20() * rm12;
        float nm11 = m01() * rm10 + m11() * rm11 + m21() * rm12;
        float nm12 = m02() * rm10 + m12() * rm11 + m22() * rm12;
        float nm13 = m03() * rm10 + m13() * rm11 + m23() * rm12;

        return dest
            .m20(m00() * rm20 + m10() * rm21 + m20() * rm22)
            .m21(m01() * rm20 + m11() * rm21 + m21() * rm22)
            .m22(m02() * rm20 + m12() * rm21 + m22() * rm22)
            .m23(m03() * rm20 + m13() * rm21 + m23() * rm22)
            .m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m03(nm03)
            .m10(nm10)
            .m11(nm11)
            .m12(nm12)
            .m13(nm13)
            .m30(m30())
            .m31(m31())
            .m32(m32())
            .m33(m33());
    }

    default IMatrix4f rotate(float ang, float x, float y, float z) {
        return rotate(ang, x, y, z, this);
    }
    
    default IMatrix4f rotate(float ang, float x, float y, float z, IMatrix4f dest) {
        if (y == 0.0f && z == 0.0f && absEqualsOne(x))
            return rotateX(x * ang, dest);
        else if (x == 0.0f && z == 0.0f && absEqualsOne(y))
            return rotateY(y * ang, dest);
        else if (x == 0.0f && y == 0.0f && absEqualsOne(z))
            return rotateZ(z * ang, dest);
        return rotateXYZ(ang, x, y, z, dest);
    }

    default IMatrix4f rotateX(float ang, IMatrix4f dest) {
        float sin = Math.sin(ang), cos = Math.cosFromSin(sin, ang);
        float lm10 = m10(), lm11 = m11(), lm12 = m12(), lm13 = m13(), lm20 = m20(), lm21 = m21(), lm22 = m22(), lm23 = m23();
        
        return dest
            .m20(Math.fma(lm10, -sin, lm20 * cos))
            .m21(Math.fma(lm11, -sin, lm21 * cos))
            .m22(Math.fma(lm12, -sin, lm22 * cos))
            .m23(Math.fma(lm13, -sin, lm23 * cos))
            .m10(Math.fma(lm10, cos, lm20 * sin))
            .m11(Math.fma(lm11, cos, lm21 * sin))
            .m12(Math.fma(lm12, cos, lm22 * sin))
            .m13(Math.fma(lm13, cos, lm23 * sin))
            .m00(m00())
            .m01(m01())
            .m02(m02())
            .m03(m03())
            .m30(m30())
            .m31(m31())
            .m32(m32())
            .m33(m33());
    }

    default IMatrix4f rotateY(float ang, IMatrix4f dest) {
        float sin = Math.sin(ang);
        float cos = Math.cosFromSin(sin, ang);

        // add temporaries for dependent values
        float nm00 = Math.fma(m00(), cos, m20() * -sin);
        float nm01 = Math.fma(m01(), cos, m21() * -sin);
        float nm02 = Math.fma(m02(), cos, m22() * -sin);
        float nm03 = Math.fma(m03(), cos, m23() * -sin);

        // set non-dependent values directly
        return dest
            .m20(Math.fma(m00(), sin, m20() * cos))
            .m21(Math.fma(m01(), sin, m21() * cos))
            .m22(Math.fma(m02(), sin, m22() * cos))
            .m23(Math.fma(m03(), sin, m23() * cos))
            // set other values
            .m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m03(nm03)
            .m10(m10())
            .m11(m11())
            .m12(m12())
            .m13(m13())
            .m30(m30())
            .m31(m31())
            .m32(m32())
            .m33(m33());
    }
    
    default IMatrix4f rotateZ(float ang, IMatrix4f dest) {
        float sin = Math.sin(ang);
        float cos = Math.cosFromSin(sin, ang);
        return rotateTowardsXY(sin, cos, dest);
    }

    default IMatrix4f rotateTowardsXY(float dirX, float dirY, IMatrix4f dest) {
        float nm00 = Math.fma(m00(), dirY, m10() * dirX);
        float nm01 = Math.fma(m01(), dirY, m11() * dirX);
        float nm02 = Math.fma(m02(), dirY, m12() * dirX);
        float nm03 = Math.fma(m03(), dirY, m13() * dirX);
        
        return dest
            .m10(Math.fma(m00(), -dirX, m10() * dirY))
            .m11(Math.fma(m01(), -dirX, m11() * dirY))
            .m12(Math.fma(m02(), -dirX, m12() * dirY))
            .m13(Math.fma(m03(), -dirX, m13() * dirY))
            .m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m03(nm03)
            .m20(m20())
            .m21(m21())
            .m22(m22())
            .m23(m23())
            .m30(m30())
            .m31(m31())
            .m32(m32())
            .m33(m33());
    }

    default IMatrix4f rotateXYZ(float ang, float x, float y, float z, IMatrix4f dest) {
        float s = Math.sin(ang);
        float c = Math.cosFromSin(s, ang);
        float C = 1.0f - c;
        
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
        
        float nm00 = m00() * rm00 + m10() * rm01 + m20() * rm02;
        float nm01 = m01() * rm00 + m11() * rm01 + m21() * rm02;
        float nm02 = m02() * rm00 + m12() * rm01 + m22() * rm02;
        float nm03 = m03() * rm00 + m13() * rm01 + m23() * rm02;
        
        float nm10 = m00() * rm10 + m10() * rm11 + m20() * rm12;
        float nm11 = m01() * rm10 + m11() * rm11 + m21() * rm12;
        float nm12 = m02() * rm10 + m12() * rm11 + m22() * rm12;
        float nm13 = m03() * rm10 + m13() * rm11 + m23() * rm12;
        
        return dest
            .m20(m00() * rm20 + m10() * rm21 + m20() * rm22)
            .m21(m01() * rm20 + m11() * rm21 + m21() * rm22)
            .m22(m02() * rm20 + m12() * rm21 + m22() * rm22)
            .m23(m03() * rm20 + m13() * rm21 + m23() * rm22)
            .m00(nm00)
            .m01(nm01)
            .m02(nm02)
            .m03(nm03)
            .m10(nm10)
            .m11(nm11)
            .m12(nm12)
            .m13(nm13)
            .m30(m30())
            .m31(m31())
            .m32(m32())
            .m33(m33());
    }

    static boolean absEqualsOne(float r) {
        return (Float.floatToRawIntBits(r) & 0x7FFFFFFF) == 0x3F800000;
    }
}
