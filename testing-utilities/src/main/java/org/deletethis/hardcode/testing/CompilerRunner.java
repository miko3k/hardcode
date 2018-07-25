package org.deletethis.hardcode.testing;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class to compile the {@link TypeSpec} into a {@link Class}.
 */
class CompilerRunner {
    private static class JavaFileObjectLoader extends ClassLoader {
        private final Map<String, JavaFileObject> objects;

        private JavaFileObjectLoader(ClassLoader parent, Map<String, JavaFileObject> objects) {
            super(parent);
            this.objects = objects;
        }

        public Class loadClass(String name) throws ClassNotFoundException {
            JavaFileObject object = objects.get(name);
            if(object == null) {
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

    public static class Input {
        private final String packageName;
        private final TypeSpec typeSpec;

        Input(String packageName, TypeSpec typeSpec) {
            this.packageName = packageName;
            this.typeSpec = typeSpec;
        }

        String getPackageName() { return packageName; }
        String getClassName() { return typeSpec.name; }
        String getFullName() { return getPackageName() + "." + getClassName(); }
    }


    private List<Class<?>> compile(List<Input> input) {
        JavaFileObject [] sourceObjects = new JavaFileObject[input.size()];
        for(int i = 0; i < input.size(); ++i) {
            Input in = input.get(i);
            sourceObjects[i] = JavaFile.builder(in.packageName, in.typeSpec).build().toJavaFileObject();
        }

        Compilation compilation = Compiler.javac().compile(sourceObjects);
        Map<String, JavaFileObject> javaFileObjectMap = new HashMap<>();

        // it might be nice to iterate over all generated files and place them into classloader, however
        // I don't know how to get FQCN from JavaFileObject
        for(Input in: input) {
            Optional<JavaFileObject> destObject = compilation.generatedFile(StandardLocation.CLASS_OUTPUT, in.getPackageName(), in.getClassName() + ".class");
            if(destObject.isPresent()) {
                javaFileObjectMap.put(in.getFullName(), destObject.get());
            } else {
                throw new AssertionError("output object not found");
            }

        }
        JavaFileObjectLoader javaFileObjectLoader = new JavaFileObjectLoader(this.getClass().getClassLoader(), javaFileObjectMap);
        List<Class<?>> output = new ArrayList<>(input.size());
        for(Input in: input) {
            try {
                output.add(javaFileObjectLoader.loadClass(in.getFullName()));
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);
            }
        }
        return output;
    }

    List<Class<?>> compile(String packageName, List<TypeSpec> typeSpecs) {
        List<Input> input = typeSpecs.stream().map(t -> new Input(packageName, t)).collect(Collectors.toList());
        return compile(input);
    }

    Class<?> compile(String packageName, TypeSpec typeSpec) {
        return compile(packageName, Collections.singletonList(typeSpec)).get(0);
    }
}
