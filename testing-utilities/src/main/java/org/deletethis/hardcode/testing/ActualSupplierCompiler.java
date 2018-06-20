package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class ActualSupplierCompiler implements SupplierCompiler {
    public <T> Supplier<T> get(TypeSpec typeSpec) {
        try {
            String packageName = "generated.files";

            CompilerRunner compilerRunner = new CompilerRunner();
            Class<?> clz = compilerRunner.compile(packageName, typeSpec);
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
