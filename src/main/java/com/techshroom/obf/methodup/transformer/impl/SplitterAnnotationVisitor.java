package com.techshroom.obf.methodup.transformer.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

final class SplitterAnnotationVisitor
        extends AnnotationVisitor {

    public static AnnotationVisitor resolve(AnnotationVisitor a,
            AnnotationVisitor b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return new SplitterAnnotationVisitor(a, b);
        }
    }

    private final AnnotationVisitor a;
    private final AnnotationVisitor b;

    public SplitterAnnotationVisitor(AnnotationVisitor a, AnnotationVisitor b) {
        super(Opcodes.ASM5);
        this.a = checkNotNull(a);
        this.b = checkNotNull(b);
    }

    @Override
    public void visit(String name, Object value) {
        this.a.visit(name, value);
        this.b.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        this.a.visitEnum(name, desc, value);
        this.b.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        AnnotationVisitor a = this.a.visitAnnotation(name, desc);
        AnnotationVisitor b = this.b.visitAnnotation(name, desc);
        return SplitterAnnotationVisitor.resolve(a, b);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return this.a.visitArray(name);
    }

    @Override
    public void visitEnd() {
        this.a.visitEnd();
        this.b.visitEnd();
    }

}
