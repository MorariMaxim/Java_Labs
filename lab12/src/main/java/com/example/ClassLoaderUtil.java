package com.example;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class ClassLoaderUtil {

    public static Class<?> loadClass(File classFile) throws ClassNotFoundException, IOException {

        String className = getFullyQualifiedNameFromClassFile(classFile);

        String ending = className.replaceAll("\\.", "\\\\") + ".class";

        File classesRootDir = new File(removeEnding(classFile.getCanonicalPath(), ending));

        System.out.println(className);

        
        URL classUrl = classesRootDir.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[] { classUrl });
 

        return classLoader.loadClass(className);
    }

    public static List<Class<?>> loadClassesFromDirectory(File directory) throws ClassNotFoundException, IOException {
        List<Class<?>> classes = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(loadClassesFromDirectory(file));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(loadClass(file));
                }
            }
        }
        return classes;
    }

    public static List<Class<?>> loadClassesFromJar(File jarFile) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        try (JarFile jar = new JarFile(jarFile)) {
            URL[] urls = { new URL("jar:file:" + jarFile.getAbsolutePath() + "!/") };
            URLClassLoader classLoader = URLClassLoader.newInstance(urls);

            jar.stream()
                    .filter(e -> e.getName().endsWith(".class"))
                    .forEach(e -> {
                        String className = e.getName().replace("/", ".").replace(".class", "");
                        try {
                            classes.add(classLoader.loadClass(className));
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    });
        }
        return classes;
    }

    public static String getFullyQualifiedNameFromClassFile(File classFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(classFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] classData = baos.toByteArray();
            
            Class<?> loadedClass = new ClassLoader() {
                public Class<?> loadClass(byte[] classData) {
                    return defineClass(null, classData, 0, classData.length);
                }
            }.loadClass(classData);

            return loadedClass.getName();
        }
    }

    public static String removeEnding(String fullPath, String ending) {
        if (fullPath == null || ending == null) {
            throw new IllegalArgumentException("Full path and ending must not be null");
        }

        if (fullPath.endsWith(ending)) {
            int endIndex = fullPath.length() - ending.length();
            return fullPath.substring(0, endIndex).replaceAll("\\$", ""); 
        } else {
            throw new IllegalArgumentException("The full path does not end with the specified ending");
        }
    }
}
