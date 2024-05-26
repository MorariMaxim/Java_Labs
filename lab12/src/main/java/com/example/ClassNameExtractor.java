package com.example;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassNameExtractor {

    public static String getFullyQualifiedNameFromJar(File jarFile, String className) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry(className.replace('.', '/') + ".class");
            if (entry == null) {
                throw new IOException("Class file not found in JAR: " + className);
            }
            return entry.getName().replace('/', '.').replace(".class", "");
        }
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

    public static void main(String[] args) throws IOException {
        File classFile = new File(".\\target\\classes\\com\\example\\ClassAnalyzer.class");
        String fullyQualifiedName = getFullyQualifiedNameFromClassFile(classFile);
        System.out.println("Fully qualified name: " + fullyQualifiedName);

        String fullPath = "d1/d2/d3/d4/file";
        String ending = "d4/file";
        try {
            String result = removeEnding(fullPath, ending);
            System.out.println("Result: " + result); // Output: d1/d2/d3
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        /*
         * File jarFile = new File("path/to/your/jarfile.jar");
         * fullyQualifiedName = getFullyQualifiedNameFromJar(jarFile,
         * "com.example.TestClass");
         * System.out.println("Fully qualified name from JAR: " + fullyQualifiedName);
         */
    }
}
