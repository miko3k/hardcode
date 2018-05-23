package org.deletethis.hardcode;

import com.squareup.javapoet.CodeBlock;

public interface CodegenContext {
    String allocateVariable(String hint);
    CodeBlock.Builder getBody();
}
