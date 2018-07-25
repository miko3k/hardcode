package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * Compiles {@link Supplier}-like classes using {@link CompilerRunner}. It's not actually important, if class
 * implements {@link Supplier}, this class will invoke any {@code get()} without arguments that is available.
 */
public class ActualSupplierCompiler implements SupplierCompiler {
    public static final String PACKAGE_NAME = "generated.files";

    public <T> Supplier<T> get(List<TypeSpec> typeSpec) {
        try {
            if(typeSpec == null || typeSpec.isEmpty()) {
                throw new NoSuchElementException("empty list");
            }

            CompilerRunner compilerRunner = new CompilerRunner();
            Class<?> clz = compilerRunner.compile(PACKAGE_NAME, typeSpec).get(0);
            Method get = clz.getMethod("get");

            Object object = clz.newInstance();
            return () -> {
                try {
                    Object value = get.invoke(object);
                    @SuppressWarnings("unchecked")
                    T result = (T) value;

                    return result;
                } catch (IllegalAccessException|InvocationTargetException e) {
                    throw new AssertionError(e);
                }
            };

        } catch (NoSuchMethodException|InstantiationException|IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }
}
