package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.Supplier;

/**
 * A class with utility methods to make writing tests easier. It servers as a single entry point for this module.
 */
public class HardcodeTesting {
    private HardcodeTesting() { }

    public static final SupplierCompiler SUPPLIER_COMPILER = new CachingSupplierCompiler(new ActualSupplierCompiler());

    public static <T> T supply(TypeSpec typeSpec) {
        Supplier<T> supplier = SUPPLIER_COMPILER.get(typeSpec);
        return supplier.get();
    }

    public static byte [] serialize(Object obj) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bytes);
            oos.writeObject(obj);
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static File outputFile(String name) {
        return TempFileFactory.getInstance().createFile(name);
    }
}
