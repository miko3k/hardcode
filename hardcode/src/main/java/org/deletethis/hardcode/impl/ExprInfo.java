package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.ObjectInfo;
import org.deletethis.hardcode.objects.Expression;

public class ExprInfo {
    private final ObjectInfo root;
    private final Expression expression;

    public ExprInfo(ObjectInfo root, Expression expression) {
        this.root = root;
        this.expression = expression;
    }

    public ObjectInfo getRoot() {
        return root;
    }

    public Expression getExpression() {
        return expression;
    }
}
