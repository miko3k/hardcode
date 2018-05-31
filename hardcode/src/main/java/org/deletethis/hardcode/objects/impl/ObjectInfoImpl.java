package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.graph.ObjectInfo;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.List;
import java.util.Objects;

public class ObjectInfoImpl implements ObjectInfo {
    private final Class<?> type;
    private final ConstructionStrategy constructionStrategy;

    public ObjectInfoImpl(Class<?> type, ConstructionStrategy constructionStrategy) {
        this.type = type;
        this.constructionStrategy = Objects.requireNonNull(constructionStrategy);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Expression getCode(CodegenContext context, List<Expression> arguments) {
        return constructionStrategy.getCode(context, arguments);
    }

    @Override
    public String toString() {
        return constructionStrategy.toString();
    }
}
