package org.deletethis.hardcode.util;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.ProcedureContext;

class SplitHelperImpl implements SplitHelper {
    private static final String PARAM = "out";

    private CodegenContext parentContext;
    private ProcedureContext currentContext;
    private int split;
    private String builder;
    private Class<?> builderType;
    private int currentFill;

    SplitHelperImpl(CodegenContext parentContext, String builder, Class<?> builderType, int split) {
        this.parentContext = parentContext;
        this.split = split;
        this.builder = builder;
        this.builderType = builderType;
        this.currentContext = null;
    }

    public String getBuilder() {
        return PARAM;
    }

    private void addMethod() {
        parentContext.addStatement("$L.$L($L)", currentContext.getClassName(), currentContext.getMethodName(), builder);
        currentContext.finish();
        currentContext = null;

    }

    public void addStatement(String format, Object... args) {
        if(currentContext != null && currentFill >= split) {
            addMethod();
        }

        if(currentContext == null) {
            currentContext = parentContext.createProcedure("partial" + builderType.getSimpleName(), PARAM, builderType);
            currentFill = 0;
        }

        currentContext.addStatement(format, args);
        ++currentFill;
    }

    public void finish() {
        if(currentContext != null) {
            addMethod();
        }
    }
}
