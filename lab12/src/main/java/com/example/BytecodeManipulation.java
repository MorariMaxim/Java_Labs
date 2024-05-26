package com.example;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class BytecodeManipulation {
    public static void main(String[] args) throws Exception {
        File inputFile = new File(".\\target\\classes\\com\\testClasses\\TestClass1.class");

        byte[] modifiedClassBytes = modifyClass(inputFile);
        saveModifiedClass(inputFile, modifiedClassBytes);

        
        URLClassLoader classLoader = URLClassLoader
                .newInstance(new URL[] { (new File(".\\target\\classes")).toURI().toURL() });
        Class<?> modifiedClass = classLoader.loadClass("com.testClasses.TestClass1");
        Method testMethod = modifiedClass.getDeclaredMethod("method1");
        testMethod.invoke(modifiedClass.getDeclaredConstructor().newInstance());
        System.out.println();
    }

    public static byte[] modifyClass(File inputFile) throws IOException {
        ClassReader classReader = new ClassReader(new FileInputStream(inputFile));
        ClassWriter classWriter = new ClassWriter(ClassReader.EXPAND_FRAMES);

        classReader.accept(new ClassVisitor(Opcodes.ASM9, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                    String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (name.equals("method1")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitCode() {
                            // Inject code at the beginning of the method
                            super.visitCode();
                            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                            mv.visitLdcInsn("Code injected at the beginning of the method");
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                                    "(Ljava/lang/String;)V", false);
                        }
                    };
                }
                return mv;
            }
        }, 0);

        return classWriter.toByteArray();
    }

    public static void saveModifiedClass(File outputFile, byte[] modifiedClassBytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(modifiedClassBytes);
        }
    }
}
