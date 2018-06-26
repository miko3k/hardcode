package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.*;

import java.util.Collections;
import java.util.List;

public class NodeDefImpl implements NodeDefinition {
    private final Class<?> type;
    private final String asString;
    private final ConstructionStrategy constructionStrategy;
    private final List<NodeParameter> parameters;
    private final boolean root;

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy, List<NodeParameter> parameters, boolean root) {
        this.type = type;
        this.asString = asString;
        this.constructionStrategy = constructionStrategy;
        this.parameters = parameters;
        this.root = root;
    }

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy, List<NodeParameter> parameters) {
        this(type, asString, constructionStrategy, parameters, false);
    }

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy) {
        this(type, asString, constructionStrategy, Collections.emptyList());
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isRoot() {
        return root;
    }

    @Override
    public ConstructionStrategy getConstructionStrategy() {
        return constructionStrategy;
    }

    @Override
    public List<NodeParameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return asString;
    }
}
