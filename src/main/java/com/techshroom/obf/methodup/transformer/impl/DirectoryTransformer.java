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
                    if (name.equals("<init>") || name.equals("<clinit>")) {
                        // don't mess with init's
                        return methodWriter;
                    }
                    String ret = getNonConflictingReturn(desc);
                    boolean usingFallback = ret.equals(FALLBACK_RETURN);
                    MethodVisitor offPuttingMethodWriter =
                            super.visitMethod(access,
                                              name,
                                              ret,
                                              signature,
                                              exceptions);
                    offPuttingMethodWriter =
                            new MethodVisitor(Opcodes.ASM5,
                                    offPuttingMethodWriter) {
                                @Override
                                public void visitInsn(int opcode) {
                                    boolean isReturn =
                                            Opcodes.IRETURN <= opcode
                                                    && opcode <= Opcodes.RETURN;
                                    if (isReturn) {
                                        if (usingFallback) {
                                            opcode = Opcodes.IRETURN;
                                            super.visitLdcInsn(Integer.MAX_VALUE);
                                        } else {
                                            opcode = Opcodes.RETURN;
                                        }
                                    }
                                    super.visitInsn(opcode);
                                }
                            };
                    return SplitterMethodVisitor
                            .resolve(methodWriter, offPuttingMethodWriter);
                }

                private String getNonConflictingReturn(String desc) {
                    String returnType =
                            Type.getReturnType(desc).getDescriptor();
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
