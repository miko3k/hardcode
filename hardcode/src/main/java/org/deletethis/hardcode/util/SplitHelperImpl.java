package org.deletethis.hardcode.util;

import org.deletethis.hardcode.objects.CodegenContext;

class SplitHelperImpl implements SplitHelper {
    private static final String PARAM = "out";

    private CodegenContext parentContext;
    private CodegenContext currentContext;
    private int split;
    private String builder;
    private Class<?> builderType;
    private int currentFill;

    SplitHelperImpl(CodegenContext parentContext, int split, String builder, Class<?> builderType) {
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
        parentContext.addStatement("$L($L)", currentContext.getMethodName(), builder);
        currentContext.finish();
        currentContext = null;

    }

    public void addStatement(String format, Object... args) {
        if(currentContext != null && currentFill >= split) {
            addMethod();
        }

        if(currentContext == null) {
            currentContext = parentContext.createProcedure("parial" + builderType.getSimpleName(), PARAM, builderType);
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
