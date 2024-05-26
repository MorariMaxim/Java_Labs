package com.example;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Random;
import java.lang.reflect.Modifier;

public class TestRunner {

    public static void runTests(Class<?> clazz) {

        if (!clazz.isAnnotationPresent(com.example.TestClass.class)) {
            System.out.println("Class " + clazz.getName() + " is not annotated with @Test. Skipping...");
            return;
        }

        System.out.println("Running tests for class: " + clazz.getName());

        Method[] methods = clazz.getDeclaredMethods();
        int passed = 0;
        int failed = 0;

        for (Method method : methods) {
            if (method.isAnnotationPresent(org.junit.Test.class)) {
                System.out.println("  Test Method: " + method.getName());

                try {
                    Object instance = null;
                    if (!Modifier.isStatic(method.getModifiers())) {
                        instance = clazz.getDeclaredConstructor().newInstance();
                    }

                    Object[] params = generateMockParameters(method);
                    method.invoke(instance, params);
                    System.out.println("    Test passed.");
                    passed += 1;
                } catch (Exception e) {
                    System.out.println("    Test failed: " + e.getMessage());
                    failed += 1;
                }
            }
        }

        System.out.println(String.format("%d tests passed", passed));
        System.out.println(String.format("%d tests failed", failed));
    }

    private static Object[] generateMockParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] mockParams = new Object[parameters.length];
        Random random = new Random();

        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            if (paramType.equals(int.class)) {
                mockParams[i] = random.nextInt(100);
            } else if (paramType.equals(String.class)) {
                mockParams[i] = "mockString";
            }
            
        }
        return mockParams;
    }
}
