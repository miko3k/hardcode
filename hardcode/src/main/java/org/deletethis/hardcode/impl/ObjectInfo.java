package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.NodeDefinition;
import org.deletethis.hardcode.objects.ObjectContext;
import org.deletethis.hardcode.objects.ConstructionStrategy;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Information about object. Serves as a node of the object tree. Most of the members are package, however
 * class itself is public.
 */
public class ObjectInfo {
    private final Class<?> type;
    private final ConstructionStrategy constructionStrategy;
    private boolean root;
    private Integer split;
    private final String asString;
    private Collection<Class<? extends Throwable>> fatalExceptions;

    public ObjectInfo(Class<?> type, ConstructionStrategy constructionStrategy, boolean root, Integer split, String asString, Collection<Class<? extends Throwable>> fatalExceptions) {
        this.type = type;
        this.constructionStrategy = Objects.requireNonNull(constructionStrategy);
        this.root = root;
        this.split = split;
        this.asString = Objects.requireNonNull(asString);
        this.fatalExceptions = (fatalExceptions == null) ? Collections.emptyList() : fatalExceptions;
    }

    static ObjectInfo ofNodeDefinion(NodeDefinition def) {
        return new ObjectInfo(
                def.getType(),
                def.getConstructionStrategy(),
                false,
                null,
                def.toString(),
                def.getFatalExceptions()
        );
    }

    static ObjectInfo ofNull() {
        return new ObjectInfo(
                null,
                (a,b) -> Expression.simple("null"),
                false,
                null,
                "null",
                null);
    }

    Class<?> getType() {
        return type;
    }
    Expression getCode(CodegenContext context, ObjectContext obj) {
        return constructionStrategy.getCode(context, obj);
    }
    boolean isRoot() {
        return root;
    }

    @Override
    public String toString() {
        return asString;
    }

    Collection<Class<? extends Throwable>> getFatalExceptions() {
        return fatalExceptions;
    }

    void makeRoot() {
        this.root = true;
    }

    Integer getSplit() {
        return split;
    }

    void setSplit(Integer split) {
        this.split = split;
    }
}
