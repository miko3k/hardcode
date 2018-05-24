package org.deletethis.hardcode.codegen;

import com.squareup.javapoet.CodeBlock;

public interface CodegenContext {
    String allocateVariable(String hint);
    CodeBlock.Builder getBody();
}
