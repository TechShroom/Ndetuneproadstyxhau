package com.techshroom.obf.methodup.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.techshroom.obf.methodup.Main;
import com.techshroom.obf.methodup.test.testcases.BasicOneMethodClass;
import com.techshroom.obf.methodup.util.DestructionVisitor;

@SuppressWarnings("javadoc")
public class TestObfuscation {
    private final Path loadedPath;
    private final Path testcases = Paths.get("src/test/resources/testcases");
    private final URLClassLoader classSourceForTestCases;
    {
        // loadedPath =
        Path tmpPath = null;
        try {
            tmpPath =
                    Paths.get(getClass().getProtectionDomain().getCodeSource()
                            .getLocation().toURI());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        this.loadedPath = tmpPath;

        // classSourceForTestCases =
        URLClassLoader tmpURLClassLoader = null;
        try {
            tmpURLClassLoader =
                    new URLClassLoader(new URL[] { this.testcases.toUri()
                            .toURL() });
        } catch (Exception e) {
            fail(e.getMessage());
        }
        this.classSourceForTestCases = tmpURLClassLoader;
    }

    private Class<?> getClass(String name) {
        try {
            return this.classSourceForTestCases.loadClass(name);
        } catch (ClassNotFoundException e) {
            fail(e.getMessage());
            return null;
        }
    }

    @After
    public void clearTestCases() throws IOException {
        Files.walkFileTree(this.testcases, new DestructionVisitor());
    }

    @Before
    public void setupTestCasesFolder() throws IOException {
        Files.createDirectories(this.testcases);
    }

    @Test
    public void oneMethodClass() throws Exception {
        Main.main(this.loadedPath
                          .resolve("com/techshroom/obf/methodup/test/testcases")
                          .toAbsolutePath().toString(),
                  "src/test/resources/testcases");
        Class<?> clazz = getClass(BasicOneMethodClass.class.getName());
        clazz.getMethod("main", String[].class)
                .invoke(null, (Object) new String[] {});
    }

    @Test
    public void oneMethodClassJar() throws Exception {
        Main.main(this.loadedPath
                          .resolve("com/techshroom/obf/methodup/test/testcases")
                          .toAbsolutePath().toString(),
                  this.testcases.resolve("test.jar").toAbsolutePath()
                          .toString());
    }

}
