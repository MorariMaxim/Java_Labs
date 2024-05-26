package com.example;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;

public class JavaCompilerUtil {
    public static File compileJavaFile(File javaFile) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Java Compiler is not available");
        }

        int result = compiler.run(null, null, null, javaFile.getPath());
        if (result != 0) {
            throw new IOException("Compilation failed");
        }

        
        String classFilePath = javaFile.getPath().replace(".java", ".class");
        return new File(classFilePath);
    }
}
