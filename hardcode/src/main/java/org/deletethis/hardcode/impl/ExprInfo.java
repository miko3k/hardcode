package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.Objects;

public class ExprInfo {
    private final CodegenContext context;
    private final Expression expression;

    public ExprInfo(CodegenContext context, Expression expression) {
        this.context = context;
        this.expression = expression;
    }

    public CodegenContext getContext() {
        return context;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExprInfo)) return false;
        ExprInfo exprInfo = (ExprInfo) o;
        return Objects.equals(context, exprInfo.context) &&
                Objects.equals(expression, exprInfo.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, expression);
    }

    @Override
    public String toString() {
        return expression + " in " + context;

    }
}
