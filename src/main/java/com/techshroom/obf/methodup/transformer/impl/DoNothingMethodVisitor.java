package com.techshroom.obf.methodup.transformer.impl;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

public class DoNothingMethodVisitor
        extends MethodVisitor {

    public DoNothingMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitParameter(String name, int access) {
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        return null;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter,
            String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitAttribute(Attribute attr) {
    }

    @Override
    public void visitCode() {
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack,
            Object[] stack) {
    }

    @Override
    public void visitInsn(int opcode) {
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name,
            String desc) {
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc) {
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc, boolean itf) {
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
            Object... bsmArgs) {
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
    }

    @Override
    public void visitLabel(Label label) {
    }

    @Override
    public void visitLdcInsn(Object cst) {
    }

    @Override
    public void visitIincInsn(int var, int increment) {
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt,
            Label... labels) {
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler,
            String type) {
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature,
            Label start, Label end, int index) {
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
            TypePath typePath, Label[] start, Label[] end, int[] index,
            String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
    }

    @Override
    public void visitEnd() {
    }

}
