package com.techshroom.obf.methodup.test;

import org.junit.Test;

import com.techshroom.obf.methodup.Main;

@SuppressWarnings("javadoc")
public class TestObfuscation {

    @Test
    public void oneMethodClass() {
        Main.main("bin/com/techshroom/obf/methodup/test/testcases",
                  "src/test/resources/com/techshroom/obf/methodup/test/testcases");
    }

    @Test
    public void oneMethodClassJar() {
        Main.main("bin/com/techshroom/obf/methodup/test/testcases",
                  "src/test/resources/test.jar");
        /*
         * try { Files.deleteIfExists(Paths.get("src/test/resources/test.jar"));
         * } catch (IOException e) { }
         */
    }

}
