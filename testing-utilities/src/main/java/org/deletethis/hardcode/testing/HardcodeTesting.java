package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * A class with utility methods to make writing tests easier. It servers as a single entry point for this module.
 */
public class HardcodeTesting {
    private HardcodeTesting() { }

    public static final SupplierCompiler SUPPLIER_COMPILER = new CachingSupplierCompiler(new ActualSupplierCompiler());

    public static <T> T supply(TypeSpec typeSpec) {
        Supplier<T> supplier = SUPPLIER_COMPILER.get(Collections.singletonList(typeSpec));
        return supplier.get();
    }

    public static <T> T supply(List<TypeSpec> typeSpec) {
        Supplier<T> supplier = SUPPLIER_COMPILER.get(typeSpec);
        return supplier.get();
    }


    public static File outputFile(String name) {
        return TempFileFactory.getInstance().createFile(name);
    }
}
