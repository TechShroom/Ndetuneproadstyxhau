package com.techshroom.obf.methodup.transformer.impl;

import static com.google.common.base.Preconditions.*;

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
            ClassWriter writer =
                    new ClassWriter(ClassWriter.COMPUTE_FRAMES
                            | ClassWriter.COMPUTE_MAXS);
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
                    MethodVisitor offPuttingMethodWriter =
                            super.visitMethod(access,
                                              name,
                                              getNonConflictingReturn(desc),
                                              signature,
                                              exceptions);
                    MethodVisitor duplicator =
                            new MethodVisitor(Opcodes.ASM5, methodWriter) {
                                @Override
                                public void visitEnd() {
                                    super.visitEnd();
                                    offPuttingMethodWriter.visitEnd();
                                }
                            };
                    return duplicator;
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
            try (OutputStream stream = Files.newOutputStream(targetFile)) {
                stream.write(writer.toByteArray());
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
