package org.deletethis.hardcode.objects;

public interface ProcedureContext extends CodegenContext {
    String getMethodName();
    void finish();
}
