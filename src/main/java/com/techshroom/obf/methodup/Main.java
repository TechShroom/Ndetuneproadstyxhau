package com.techshroom.obf.methodup;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.ValueConverter;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.techshroom.obf.methodup.transformer.Transformer;
import com.techshroom.obf.methodup.transformer.TransformerProvider;
import com.techshroom.obf.methodup.util.DestructionVisitor;

/**
 * Main class for the obfuscator.
 * 
 * @author Kenzie Togami
 */
public class Main {

    private static final OptionParser PARSER = new OptionParser();

    private static final ValueConverter<Path> TOPATH =
            new ValueConverter<Path>() {

                @Override
                public Class<? extends Path> valueType() {
                    return Path.class;
                }

                @Override
                public String valuePattern() {
                    return null;
                }

                @Override
                public Path convert(String value) {
                    return Paths.get(value);
                }

            };

    private static final NonOptionArgumentSpec<Path> FILES = PARSER
            .nonOptions("<input> <output>").withValuesConvertedBy(TOPATH);

    private static final Module mainModule = new MainModule();

    /**
     * Starts the obfuscator.
     * 
     * @param args
     *            - Arguments
     */
    public static void main(String... args) {
        OptionSet opts = PARSER.parse(args);
        List<Path> files = FILES.values(opts);
        if (files.size() != 2) {
            if (files.size() != 0) {
                System.err.println("Must provide 0 or 2 arguments.");
                try {
                    PARSER.printHelpOn(System.err);
                } catch (IOException e) {
                }
                return;
            }
            files = promptForFiles();
        }
        Path input = files.get(0);
        Path output = files.get(1);
        if (Files.isDirectory(input)) {
            // scan for classes
            transformDirectory(input, output, null);
        } else if (Files.isRegularFile(input)) {
            // jar of classes, unpack and do above
            try {
                Path tempDir = Files.createTempDirectory("tsobfin");
                try (JarFile jar = new JarFile(input.toFile())) {
                    jar.stream().forEach(e -> {
                        if (e.isDirectory()) {
                            return;
                        }
                        try {
                            Path resolvedPath = tempDir.resolve(e.getName());
                            Files.createDirectories(resolvedPath.getParent());
                            ByteStreams.copy(jar.getInputStream(e), Files
                                    .newOutputStream(resolvedPath));
                        } catch (Exception e1) {
                            throw Throwables.propagate(e1);
                        }
                    });
                    transformDirectory(tempDir, output, jar);
                } finally {
                    Files.walkFileTree(tempDir, new DestructionVisitor());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            // wat
            throw new IllegalArgumentException(input
                    + " couldn't be scanned for classes");
        }
    }

    @SuppressWarnings("resource")
    private static void transformDirectory(Path input, Path output,
            JarFile jarMetaData) {
        boolean jarIt = output.toString().endsWith(".jar");
        Optional<JarFile> inputJar = Optional.ofNullable(jarMetaData);
        Path outDir;
        if (jarIt) {
            try {
                outDir = Files.createTempDirectory("tsobfout");
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        } else {
            // we assume it's a directory
            outDir = output;
        }
        try {
            Files.createDirectories(outDir);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        Injector injector = Guice.createInjector(mainModule);
        Transformer transformer =
                injector.getInstance(TransformerProvider.class)
                        .getDirectoryTransformer(input, outDir);
        transformer.transform();
        if (jarIt) {
            try {
                Files.deleteIfExists(output);
            } catch (IOException e1) {
                throw Throwables.propagate(e1);
            }
            try (DirectoryStream<Path> stream =
                    Files.newDirectoryStream(outDir, Files::isRegularFile);
                    JarOutputStream outputJar =
                            new JarOutputStream(Files.newOutputStream(output))) {
                // two paths, one for input jar and one for input directory
                if (inputJar.isPresent()) {
                    // iterate by entries
                    inputJar.get().stream().forEach(e -> {
                        try {
                            Path res = outDir.resolve(e.getName());
                            JarEntry replace = new JarEntry(e);
                            outputJar.putNextEntry(replace);
                            InputStream source;
                            if (Files.exists(res)) {
                                // only replace existing files
                                source = Files.newInputStream(res);
                                // update time
                                replace.setTime(System.currentTimeMillis());
                            } else {
                                // copy non-existent files
                                source = inputJar.get().getInputStream(e);
                            }
                            ByteStreams.copy(source, outputJar);
                            source.close();
                            outputJar.closeEntry();
                        } catch (Exception ex) {
                            throw Throwables.propagate(ex);
                        }
                    });
                } else {
                    for (Path p : stream) {
                        String name = p.relativize(outDir).toString();
                        JarEntry newEntry = new JarEntry(name);
                        try (InputStream source = Files.newInputStream(p)) {
                            outputJar.putNextEntry(newEntry);
                            ByteStreams.copy(source, outputJar);
                            outputJar.closeEntry();
                        }
                    }
                }
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    private static List<Path> promptForFiles() {
        // we don't want to close standard input
        @SuppressWarnings("resource")
        Scanner in = new Scanner(System.in);
        System.out.print("Input Location: ");
        Path inputPath = TOPATH.convert(in.nextLine());
        System.out.print("Output Location: ");
        Path outputPath = TOPATH.convert(in.nextLine());
        return ImmutableList.of(inputPath, outputPath);
    }

}
