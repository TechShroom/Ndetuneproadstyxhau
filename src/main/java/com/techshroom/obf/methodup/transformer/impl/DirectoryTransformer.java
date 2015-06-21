package com.techshroom.obf.methodup.transformer.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.google.common.base.Throwables;
import com.techshroom.obf.methodup.transformer.Transformer;

final class DirectoryTransformer implements Transformer {

    private class TransformingVisitor
            extends SimpleFileVisitor<Path> {
        private final Path in = DirectoryTransformer.this.inputDirectory
                .toAbsolutePath();
        private final Path out = DirectoryTransformer.this.outputDirectory
                .toAbsolutePath();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            super.visitFile(file, attrs);
            if (!file.toString().endsWith(".class")) {
                return FileVisitResult.CONTINUE;
            }
            Path absolutePath = file.toAbsolutePath();
            checkState(absolutePath.startsWith(this.in), "outside source");
            Path targetFile =
                    this.out.resolve(absolutePath.toString()
                            .replace(this.in.toString(), "."));
            transform(file, targetFile);
            return FileVisitResult.CONTINUE;
        }

    }

    private final Path inputDirectory;
    private final Path outputDirectory;

    DirectoryTransformer(Path inputDirectory, Path outputDirectory) {
        checkArgument(Files.isDirectory(inputDirectory),
                      "%s must be a directory",
                      inputDirectory);
        checkArgument(Files.isDirectory(outputDirectory),
                      "%s must be a directory",
                      outputDirectory);
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void transform() {
        try {
            Files.walkFileTree(this.inputDirectory, new TransformingVisitor());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void transform(Path file, Path targetFile) {
        byte[] classSource;
        try {
            classSource = Files.readAllBytes(file);
            ClassReader reader = new ClassReader(classSource);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, writer) {
                private static final String TARGET_RETURN = "V";
                private static final String FALLBACK_RETURN = "I";

                @Override
                public MethodVisitor visitMethod(int access, String name,
                        String desc, String signature, String[] exceptions) {
                    MethodVisitor methodWriter =
                            super.visitMethod(access,
                                              name,
                                              desc,
                                              signature,
                                              exceptions);
                    methodWriter =
                            new MethodVisitor(Opcodes.ASM5, methodWriter) {

                                @Override
                                public void visitMaxs(int maxStack,
                                        int maxLocals) {
                                    super.visitMaxs(0, 0);
                                }

                                @Override
                                public void visitInsn(int opcode) {
                                    super.visitInsn(opcode);
                                }

                            };
                    if (name.equals("<init>") || name.equals("<clinit>")) {
                        // don't mess with init's
                        return methodWriter;
                    }
                    System.err.println(file);
                    String ret = getNonConflictingReturn(desc);
                    boolean usingFallback = ret.endsWith(FALLBACK_RETURN);
                    MethodVisitor offPuttingMethodWriter =
                            super.visitMethod(access,
                                              name,
                                              ret,
                                              null,
                                              exceptions);
                    offPuttingMethodWriter =
                            new DoNothingMethodVisitor(Opcodes.ASM5,
                                    offPuttingMethodWriter) {

                                private boolean hitReturn = false;

                                @Override
                                public void visitMaxs(int maxStack,
                                        int maxLocals) {
                                    this.mv.visitMaxs(0, 0);
                                }

                                @Override
                                public void visitInsn(int opcode) {
                                    if (this.hitReturn) {
                                        return;
                                    }
                                    boolean isReturn =
                                            Opcodes.IRETURN <= opcode
                                                    && opcode <= Opcodes.RETURN;
                                    boolean isVoidReturn =
                                            opcode == Opcodes.RETURN;
                                    if (isReturn) {
                                        this.hitReturn = true;
                                        checkState(isVoidReturn == usingFallback,
                                                   "impossible state, report issue with code"
                                                           + " (method %s; desc %s; file %s)",
                                                   name,
                                                   desc,
                                                   file);
                                        if (!usingFallback) {
                                            opcode = Opcodes.RETURN;
                                        } else {
                                            this.mv.visitInsn(Opcodes.ICONST_0);
                                            opcode = Opcodes.IRETURN;
                                        }
                                        this.mv.visitInsn(opcode);
                                    }
                                }
                            };
                    return SplitterMethodVisitor
                            .resolve(methodWriter, offPuttingMethodWriter);
                }

                private String getNonConflictingReturn(String desc) {
                    String returnType =
                            Type.getReturnType(desc).getDescriptor();
                    System.err
                            .println(desc
                                    + "->"
                                    + desc.substring(0, desc.indexOf(')') + 1)
                                    + (returnType.equals(TARGET_RETURN)
                                                                       ? FALLBACK_RETURN
                                                                       : TARGET_RETURN));
                    return desc.substring(0, desc.indexOf(')') + 1)
                            + (returnType.equals(TARGET_RETURN)
                                                               ? FALLBACK_RETURN
                                                               : TARGET_RETURN);
                }
            };
            reader.accept(classVisitor, 0);
            Files.createDirectories(targetFile.getParent());
            try (OutputStream stream = Files.newOutputStream(targetFile)) {
                stream.write(writer.toByteArray());
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
