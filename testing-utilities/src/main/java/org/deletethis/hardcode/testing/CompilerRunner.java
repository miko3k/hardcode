package org.deletethis.hardcode.testing;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Optional;

/**
 * Helper class to compile the {@link TypeSpec} into a {@link Class}.
 */
class CompilerRunner {
    private static class JavaFileObjectLoader extends ClassLoader {
        private final JavaFileObject object;
        private final String name;

        private JavaFileObjectLoader(ClassLoader parent, JavaFileObject object, String name) {
            super(parent);
            this.object = object;
            this.name = name;
        }

        public Class loadClass(String name) throws ClassNotFoundException {
            if(!this.name.equals(name)) {
                return super.loadClass(name);
            }

            try {
                InputStream input = object.openInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = input.read(data, 0, data.length)) >= 0) {
                    buffer.write(data, 0, nRead);
                }

                input.close();

                byte[] classData = buffer.toByteArray();
                return defineClass(name, classData, 0, classData.length);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    Class<?> compile(String packageName, TypeSpec typeSpec) {
        String className = typeSpec.name;
        String fullName = packageName + "." + className;

        JavaFileObject sourceObject = JavaFile.builder(packageName, typeSpec).build().toJavaFileObject();

        Compilation compilation = Compiler.javac().compile(sourceObject);
        Optional<JavaFileObject> destObject = compilation.generatedFile(StandardLocation.CLASS_OUTPUT, packageName, className + ".class");

        if(destObject.isPresent()) {
            JavaFileObjectLoader classLoader = new JavaFileObjectLoader(this.getClass().getClassLoader(), destObject.get(), fullName);
            try {
                return classLoader.loadClass(fullName);
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);
            }
        } else {
            throw new AssertionError("output object not found");
        }
    }
}
