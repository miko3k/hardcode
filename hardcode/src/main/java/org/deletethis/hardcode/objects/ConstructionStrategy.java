package org.deletethis.hardcode.objects;

public interface ConstructionStrategy {
    Expression getCode(CodegenContext context, ObjectContext obj);
}
