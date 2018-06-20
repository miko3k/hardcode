package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import javax.xml.ws.Provider;
import java.util.function.Supplier;

/**
 * Instantiates a {@link Supplier} defined in {@link TypeSpec}. Provided {@link TypeSpec} may either define an
 * actual {@link Supplier} or just something that has <tt>get</tt> method.
 */
public interface SupplierCompiler {
    <T> Supplier<T> get(TypeSpec typeSpec);
}
