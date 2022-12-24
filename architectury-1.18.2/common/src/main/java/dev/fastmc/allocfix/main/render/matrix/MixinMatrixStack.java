package dev.fastmc.allocfix.main.render.matrix;

import dev.fastmc.allocfix.IMatrix3f;
import dev.fastmc.allocfix.IMatrix4f;
import dev.fastmc.allocfix.IPatchedMatrixStack;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(MatrixStack.class)
public class MixinMatrixStack implements IPatchedMatrixStack {
    @Shadow
    @Final
    private Deque<MatrixStack.Entry> stack;

    private final ArrayDeque<MatrixStack.Entry> pool = new ArrayDeque<>();

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void push() {
        MatrixStack.Entry entry = this.stack.getLast();
        MatrixStack.Entry newEntry = this.pool.pollLast();
        if (newEntry == null) {
            newEntry = new MatrixStack.Entry(new Matrix4f(), new Matrix3f());
        }
        ((IMatrix4f) (Object) newEntry.getPositionMatrix()).set((IMatrix4f) (Object) entry.getPositionMatrix());
        ((IMatrix3f) (Object) newEntry.getNormalMatrix()).set((IMatrix3f) (Object) entry.getNormalMatrix());
        this.stack.addLast(newEntry);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void pop() {
        MatrixStack.Entry entry = this.stack.removeLast();
        pool.addLast(entry);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void translate(double x, double y, double z) {
        MatrixStack.Entry entry = this.stack.getLast();
        ((IMatrix4f) (Object) entry.getPositionMatrix()).translate((float) x, (float) y, (float) z);
    }

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Overwrite
    public void scale(float x, float y, float z) {
        MatrixStack.Entry entry = this.stack.getLast();
        ((IMatrix4f) (Object) entry.getPositionMatrix()).scale(x, y, z);

        if (x == y && y == z) {
            if (x > 0.0F) {
                return;
            }
            entry.getNormalMatrix().multiply(-1.0F);
        }

        x = 1.0f / x;
        y = 1.0f / y;
        z = 1.0f / z;
        float i = MathHelper.fastInverseCbrt(x * y * z);
        ((IMatrix3f) (Object) entry.getNormalMatrix()).scale(i * x, i * y, i * z);
    }

    @Override
    public void rotate(float angle, float x, float y, float z) {
        MatrixStack.Entry entry = this.stack.getLast();
        ((IMatrix4f) (Object) entry.getPositionMatrix()).rotate(angle, x, y, z);
        ((IMatrix3f) (Object) entry.getNormalMatrix()).rotate(angle, x, y, z);
    }

    @Override
    public void rotateX(float angle) {
        MatrixStack.Entry entry = this.stack.getLast();
        ((IMatrix4f) (Object) entry.getPositionMatrix()).rotateX(angle);
        ((IMatrix3f) (Object) entry.getNormalMatrix()).rotateX(angle);
    }

    @Override
    public void rotateY(float angle) {
        MatrixStack.Entry entry = this.stack.getLast();
        ((IMatrix4f) (Object) entry.getPositionMatrix()).rotateY(angle);
        ((IMatrix3f) (Object) entry.getNormalMatrix()).rotateY(angle);
    }

    @Override
    public void rotateZ(float angle) {
        MatrixStack.Entry entry = this.stack.getLast();
        ((IMatrix4f) (Object) entry.getPositionMatrix()).rotateZ(angle);
        ((IMatrix3f) (Object) entry.getNormalMatrix()).rotateZ(angle);
    }
}
