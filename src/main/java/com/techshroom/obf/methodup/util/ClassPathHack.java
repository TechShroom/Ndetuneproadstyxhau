package com.techshroom.obf.methodup.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * For injecting classpath entries.
 * 
 * @author Kenzie Togami
 */
public class ClassPathHack {
    private static final Class<?>[] parameters = new Class[] { URL.class };

    /**
     * Adds the file represented by the given String to the classpath.
     * 
     * @param s
     *            - a file, represented as a String
     */
    public static void addFile(String s) {
        addFile(Paths.get(s));
    }

    /**
     * Adds the given {@link Path} to the classpath.
     * 
     * @param path
     *            - path entry to add
     */
    public static void addFile(Path path) {
        try {
            addURL(path.toUri().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the given {@link URL} to the classpath.
     * 
     * @param u
     *            - a URL
     */
    public static void addURL(URL u) {
        URLClassLoader sysloader =
                (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { u });
        } catch (Throwable t) {
            throw new RuntimeException(
                    "Error, could not add URL to system classloader", t);
        }

        System.setProperty("java.class.path",
                           System.getProperty("java.class.path")
                                   + File.pathSeparator
                                   + u.getFile()
                                           .replace('/', File.separatorChar)
                                           .substring(1).replace("%20", " "));
    }
}