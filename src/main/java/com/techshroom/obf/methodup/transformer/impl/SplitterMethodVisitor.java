package com.techshroom.obf.methodup.transformer.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

final class SplitterMethodVisitor
        extends MethodVisitor {

    public static MethodVisitor resolve(MethodVisitor a, MethodVisitor b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return new SplitterMethodVisitor(a, b);
        }
    }

    private final MethodVisitor a;
    private final MethodVisitor b;

    public SplitterMethodVisitor(MethodVisitor a, MethodVisitor b) {
        super(Opcodes.ASM5);
        this.a = checkNotNull(a);
        this.b = checkNotNull(b);
    }

    @Override
    public void visitParameter(String name, int access) {
        this.a.visitParameter(name, access);
        this.b.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        AnnotationVisitor a = this.a.visitAnnotationDefault();
        AnnotationVisitor b = this.b.visitAnnotationDefault();
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor a = this.a.visitAnnotation(desc, visible);
        AnnotationVisitor b = this.b.visitAnnotation(desc, visible);
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor a =
                this.a.visitTypeAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor b =
                this.b.visitTypeAnnotation(typeRef, typePath, desc, visible);
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter,
            String desc, boolean visible) {
        AnnotationVisitor a =
                this.a.visitParameterAnnotation(parameter, desc, visible);
        AnnotationVisitor b =
                this.b.visitParameterAnnotation(parameter, desc, visible);
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        this.a.visitAttribute(attr);
        this.b.visitAttribute(attr);
    }

    @Override
    public void visitCode() {
        this.a.visitCode();
        this.b.visitCode();
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack,
            Object[] stack) {
        this.a.visitFrame(type, nLocal, local, nStack, stack);
        this.b.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        this.a.visitInsn(opcode);
        this.b.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        this.a.visitIntInsn(opcode, operand);
        this.b.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        this.a.visitVarInsn(opcode, var);
        this.b.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        this.a.visitTypeInsn(opcode, type);
        this.b.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name,
            String desc) {
        this.a.visitFieldInsn(opcode, owner, name, desc);
        this.b.visitFieldInsn(opcode, owner, name, desc);
    }

    @Deprecated
    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc) {
        this.a.visitMethodInsn(opcode, owner, name, desc);
        this.b.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc, boolean itf) {
        this.a.visitMethodInsn(opcode, owner, name, desc, itf);
        this.b.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
            Object... bsmArgs) {
        this.a.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        this.b.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.a.visitJumpInsn(opcode, label);
        this.b.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        this.a.visitLabel(label);
        this.b.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        this.a.visitLdcInsn(cst);
        this.b.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        this.a.visitIincInsn(var, increment);
        this.b.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt,
            Label... labels) {
        this.a.visitTableSwitchInsn(min, max, dflt, labels);
        this.b.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.a.visitLookupSwitchInsn(dflt, keys, labels);
        this.b.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.a.visitMultiANewArrayInsn(desc, dims);
        this.b.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor a =
                this.a.visitInsnAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor b =
                this.b.visitInsnAnnotation(typeRef, typePath, desc, visible);
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler,
            String type) {
        this.a.visitTryCatchBlock(start, end, handler, type);
        this.b.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor a =
                this.a.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor b =
                this.b.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature,
            Label start, Label end, int index) {
        this.a.visitLocalVariable(name, desc, signature, start, end, index);
        this.b.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
            TypePath typePath, Label[] start, Label[] end, int[] index,
            String desc, boolean visible) {
        AnnotationVisitor a =
                this.a.visitLocalVariableAnnotation(typeRef,
                                                    typePath,
                                                    start,
                                                    end,
                                                    index,
                                                    desc,
                                                    visible);
        AnnotationVisitor b =
                this.b.visitLocalVariableAnnotation(typeRef,
                                                    typePath,
                                                    start,
                                                    end,
                                                    index,
                                                    desc,
                                                    visible);
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.a.visitLineNumber(line, start);
        this.b.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.a.visitMaxs(maxStack, maxLocals);
        this.b.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        this.a.visitEnd();
        this.b.visitEnd();
    }

}
