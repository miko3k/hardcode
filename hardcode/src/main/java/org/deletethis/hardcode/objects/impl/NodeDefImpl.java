package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.*;

import java.util.Collections;
import java.util.List;

public class NodeDefImpl implements NodeDefinition {
    private final ObjectInfo objectInfo;
    private final List<NodeParameter> parameters;

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy, List<NodeParameter> parameters, boolean root) {
        this.parameters = parameters;
        this.objectInfo = new ObjectInfo() {
            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public Expression getCode(CodegenContext context, ObjectContext obj) {
                return constructionStrategy.getCode(context, obj);
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

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy, List<NodeParameter> parameters) {
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
    public List<NodeParameter> getParameters() {
        return parameters;
    }
}
