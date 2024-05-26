package com.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path of the class file, directory, or jar file:");
        // String inputPath = scanner.nextLine();
        // String inputPath = ".\\target\\classes\\com\\example\\ClassAnalyzer.class";
        // String inputPath = ".\\target\\classes\\com\\testClasses\\";
        String inputPath = ".\\src\\main\\java\\com\\testClasses\\TestClass1.java";
        // String inputPath = ".\\target\\classes\\com\\testClasses\\TestClass1.class";
        // String inputPath = "lab12.jar";

        File inputFile = new File(inputPath);

        List<Class<?>> classes = new ArrayList<>();

        if (inputFile.isFile()) {
            if (inputFile.getName().endsWith(".class")) {
                classes.add(ClassLoaderUtil.loadClass(inputFile));
            } else if (inputFile.getName().endsWith(".jar")) {
                classes.addAll(ClassLoaderUtil.loadClassesFromJar(inputFile));
            } else if (inputFile.getName().endsWith(".java")) {
                File compiledClass = JavaCompilerUtil.compileJavaFile(inputFile);
                classes.add(ClassLoaderUtil.loadClass(compiledClass));
            }

        } else if (inputFile.isDirectory()) {
            classes.addAll(ClassLoaderUtil.loadClassesFromDirectory(inputFile));
        }

        for (Class<?> clazz : classes) {
            ClassAnalyzer.analyzeClass(clazz);
            TestRunner.runTests(clazz);
        }
        scanner.close();

    }
}
