package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.graph.ObjectInfo;
import org.deletethis.hardcode.objects.NodeDef;

import java.util.Collections;
import java.util.List;

public class NodeDefImpl implements NodeDef {
    private final ObjectInfo objectInfo;
    private final List<Object> parameters;

    public NodeDefImpl(ObjectInfo objectInfo, List<Object> parameters) {
        this.objectInfo = objectInfo;
        this.parameters = parameters;
    }

    public NodeDefImpl(Class<?> type, List<Object> parameters, ConstructionStrategy constructionStrategy) {
        this(new ObjectInfoImpl(type, constructionStrategy), parameters);
    }

    public NodeDefImpl(Class<?> type, ConstructionStrategy constructionStrategy) {
        this(new ObjectInfoImpl(type, constructionStrategy), Collections.emptyList());
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
