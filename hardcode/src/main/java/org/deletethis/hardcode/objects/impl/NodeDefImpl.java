package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.*;

import java.util.*;

public class NodeDefImpl implements NodeDefinition {
    private final Class<?> type;
    private final String asString;
    private final ConstructionStrategy constructionStrategy;
    private final List<NodeParameter> parameters;
    private Set<Class<? extends Throwable>> fatalExceptions = new HashSet<>();
    private boolean root;

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy, List<NodeParameter> parameters) {
        this.type = type;
        this.asString = asString;
        this.constructionStrategy = constructionStrategy;
        this.parameters = parameters;
    }

    public NodeDefImpl(Class<?> type, String asString, ConstructionStrategy constructionStrategy) {
        this(type, asString, constructionStrategy, Collections.emptyList());
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public void addFatalException(Class<? extends Throwable> exceptionClass) {
        fatalExceptions.add(Objects.requireNonNull(exceptionClass));
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
    public Collection<Class<? extends Throwable>> getFatalExceptions() {
        return fatalExceptions;
    }

    @Override
    public String toString() {
        return asString;
    }
}
