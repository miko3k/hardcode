package org.deletethis.hardcode.objects;

public interface CodegenContext {
    String allocateVariable(Class<?> hint);
    void addStatement(String format, Object... args);
    CodegenContext createVoidMethod(String nameHint, String paramName, Class<?> paramType);
    String getMethodName();
    void finish();
}
