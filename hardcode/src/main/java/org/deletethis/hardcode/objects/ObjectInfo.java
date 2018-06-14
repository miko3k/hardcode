package org.deletethis.hardcode.objects;

public interface ObjectInfo {
    Class<?> getType();
    Expression getCode(CodegenContext context, ObjectContext obj);
    boolean isRoot();
    @Override
    String toString();
}
