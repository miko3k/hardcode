package org.deletethis.hardcode.util;

import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ProcedureContext;

class SplitHelperImpl implements SplitHelper {
    private static final String PARAM = "out";

    private CodegenContext parentContext;
    private ProcedureContext currentContext;
    private int split;
    private String builder;
    private Class<?> builderType;
    private int currentFill;
    private boolean inStatement;

    SplitHelperImpl(CodegenContext parentContext, String builder, Class<?> builderType, int split) {
        this.parentContext = parentContext;
        this.split = split;
        this.builder = builder;
        this.builderType = builderType;
        this.currentContext = null;
        this.inStatement = false;
    }

    public String getBuilder() {
        return PARAM;
    }

    private void addMethod() {
        Expression ex = currentContext.getCallExpression(builder);
        parentContext.addStatement("$L", ex.getCode(parentContext.getClassName()));
        currentContext.finish();
        currentContext = null;
    }

    @Override
    public String getClassName() {
        if(!inStatement)
            throw new IllegalStateException();

        return currentContext.getClassName();
    }


    @Override
    public void prepareStatement() {
        if(inStatement)
            throw new IllegalStateException();

        if(currentContext != null && currentFill >= split) {
            addMethod();
        }

        if(currentContext == null) {
            currentContext = parentContext.createProcedure("partial" + builderType.getSimpleName(), PARAM, builderType);
            currentFill = 0;
        }

        inStatement = true;
    }

    public void addStatement(String format, Object... args) {
        if(!inStatement)
            throw new IllegalStateException();

        currentContext.addStatement(format, args);
        ++currentFill;

        inStatement = false;
    }

    public void finish() {
        if(inStatement)
            throw new IllegalStateException();

        if(currentContext != null) {
            addMethod();
        }
    }
}
