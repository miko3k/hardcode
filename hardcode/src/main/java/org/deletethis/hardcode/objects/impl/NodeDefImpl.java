package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.NodeDefinition;

import java.util.Collections;
import java.util.List;

public class NodeDefImpl implements NodeDefinition {
    private final ObjectInfo objectInfo;
    private final List<Object> parameters;

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy, List<Object> parameters, boolean root) {
        this.parameters = parameters;
        this.objectInfo = new ObjectInfo() {
            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public Expression getCode(CodegenContext context, List<Expression> arguments) {
                return constructionStrategy.getCode(type, context, arguments);
            }

            @Override
            public boolean isRoot() {
                return root;
            }

            @Override
            public String toString() {
                return asString;
            }
        };
    }

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy, List<Object> parameters) {
        this(type, asString, constructionStrategy, parameters, false);
    }

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy) {
        this(type, asString, constructionStrategy, Collections.emptyList());
    }

    @Override
    public ObjectInfo getObjectInfo() {
        return objectInfo;
    }

    @Override
    public List<Object> getParameters() {
        return parameters;
    }
}
