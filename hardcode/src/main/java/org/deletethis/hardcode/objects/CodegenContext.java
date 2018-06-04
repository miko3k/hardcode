package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;

public interface CodegenContext {
    String allocateVariable(Class<?> hint);
    void addStatement(String format, Object... args);
}
