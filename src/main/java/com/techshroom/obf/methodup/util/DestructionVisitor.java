package com.techshroom.obf.methodup.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A FileVisitor that destroys all files that is finds.
 * 
 * @author Kenzie Togami
 */
public class DestructionVisitor
        extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
        Files.deleteIfExists(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
            throws IOException {
        if (exc != null) {
            throw exc;
        }
        Files.deleteIfExists(dir);
        return FileVisitResult.CONTINUE;
    }

}
