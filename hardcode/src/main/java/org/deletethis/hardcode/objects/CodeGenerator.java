package org.deletethis.hardcode.objects;

public interface CodeGenerator {
    Expression getCode(CodegenContext context, CodegenParameters obj);
}
