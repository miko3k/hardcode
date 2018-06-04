package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.Objects;
import java.util.Optional;

public class ExprInfo {
    private final Expression expression;
    private final CodegenContext context;

    public ExprInfo(Expression expression, CodegenContext context) {
        this.expression = Objects.requireNonNull(expression);
        this.context = Objects.requireNonNull(context);
    }

    public Expression getExpression() {
        return expression;
    }

    public CodegenContext getContext() {
        return context;
    }
}
