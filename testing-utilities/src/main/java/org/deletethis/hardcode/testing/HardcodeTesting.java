package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

    private static final SupplierCompiler RAW_COMPILER = new ActualSupplierCompiler();
    private static final SupplierCompiler SUPPLIER_COMPILER = new CachingSupplierCompiler(RAW_COMPILER);

    public static <T> T supply(TypeSpec typeSpec) {
        Supplier<T> supplier = SUPPLIER_COMPILER.get(Collections.singletonList(typeSpec));
        return supplier.get();
    }

    public static <T> T supplyUncached(TypeSpec typeSpec) {
        Supplier<T> supplier = RAW_COMPILER.get(Collections.singletonList(typeSpec));
        return supplier.get();
    }


    public static <T> T supply(List<TypeSpec> typeSpec) {
        Supplier<T> supplier = SUPPLIER_COMPILER.get(typeSpec);
        return supplier.get();
    }


    public static File outputFile(String name) {
        return TempFileFactory.getInstance().createFile(name);
    }

    public static void deleteRecursively(File file) {
        if(!file.isDirectory())
            throw new IllegalArgumentException("should be a directory");

        Path directory = file.toPath();
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
