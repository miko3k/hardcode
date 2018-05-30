package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;

public interface CodegenContext {
    String allocateVariable(String hint);
    CodeBlock.Builder getBody();
}
