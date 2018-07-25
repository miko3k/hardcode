package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import javax.xml.ws.Provider;
import java.util.List;
import java.util.function.Supplier;

/**
 * Instantiates a {@link Supplier} defined in {@link TypeSpec}. Provided {@link TypeSpec} may either define an
 * actual {@link Supplier} or just something that has {@code get} method.
 *
 * Actual class must be first member of given list, other are auxiliary classes required for main class to work.
 */
public interface SupplierCompiler {
    <T> Supplier<T> get(List<TypeSpec> typeSpec);
}
