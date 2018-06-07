package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.Objects;

public class ExprInfo {
    private final Divertex<ObjectInfo> root;
    private final Expression expression;

    public ExprInfo(Divertex<ObjectInfo> root, Expression expression) {
        this.root = root;
        this.expression = expression;
    }

    public Divertex<ObjectInfo> getRoot() {
        return root;
    }

    public Expression getExpression() {
        return expression;
    }
}
