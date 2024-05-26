package com.example;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ClassAnalyzer {

    public static void analyzeClass(Class<?> clazz) {
        System.out.println("Class: " + clazz.getName());

        // Superclass
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            System.out.println("Superclass: " + superclass.getName());
        }

        // Interfaces
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            System.out.print("Interfaces: ");
            for (Class<?> iface : interfaces) {
                System.out.print(iface.getName() + " ");
            }
            System.out.println();
        }

        // Fields
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            System.out.println("Fields:");
            for (Field field : fields) {
                System.out.println("  " + Modifier.toString(field.getModifiers()) + " " + field.getType().getName()
                        + " " + field.getName());
            }
        }

        // Constructors
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 0) {
            System.out.println("Constructors:");
            for (Constructor<?> constructor : constructors) {
                System.out.print(
                        "  " + Modifier.toString(constructor.getModifiers()) + " " + clazz.getSimpleName() + "(");
                Class<?>[] paramTypes = constructor.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    System.out.print(paramTypes[i].getName());
                    if (i < paramTypes.length - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println(")");
            }
        }

        // Methods
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length > 0) {
            System.out.println("Methods:");
            for (Method method : methods) {
                System.out.print("  " + Modifier.toString(method.getModifiers()) + " "
                        + method.getReturnType().getName() + " " + method.getName() + "(");
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    System.out.print(paramTypes[i].getName());
                    if (i < paramTypes.length - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println(")");
            }
        }
    }
}
