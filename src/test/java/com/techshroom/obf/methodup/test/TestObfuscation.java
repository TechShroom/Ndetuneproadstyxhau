package com.techshroom.obf.methodup.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.techshroom.obf.methodup.Main;
import com.techshroom.obf.methodup.util.DestructionVisitor;

@SuppressWarnings("javadoc")
public class TestObfuscation {
    private final Path testcases = Paths.get("src/test/resources/testcases");

    @After
    public void clearTestCases() throws IOException {
        Files.walkFileTree(this.testcases, new DestructionVisitor());
    }

    @Before
    public void setupTestCasesFolder() throws IOException {
        Files.createDirectories(this.testcases);
    }

    @Test
    public void oneMethodClass() {
        Main.main("bin/com/techshroom/obf/methodup/test/testcases",
                  "src/test/resources/testcases");
    }

    @Test
    public void oneMethodClassJar() {
        Main.main("bin/com/techshroom/obf/methodup/test/testcases",
                  this.testcases.resolve("test.jar").toAbsolutePath()
                          .toString());
    }

}
