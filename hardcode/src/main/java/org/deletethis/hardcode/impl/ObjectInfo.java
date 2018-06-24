package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.NodeDefinition;
import org.deletethis.hardcode.objects.ObjectContext;
import org.deletethis.hardcode.objects.impl.ConstructionStrategy;

public class ObjectInfo {
    private final Class<?> type;
    private final ConstructionStrategy constructionStrategy;
    private boolean root;
    private Integer split;
    private final String asString;

    private ObjectInfo(Class<?> type, ConstructionStrategy constructionStrategy, boolean root, Integer split, String asString) {
        this.type = type;
        this.constructionStrategy = constructionStrategy;
        this.root = root;
        this.split = split;
        this.asString = asString;
    }

    static ObjectInfo ofNodeDefinion(NodeDefinition def) {
        return new ObjectInfo(
                def.getType(),
                def.getConstructionStrategy(),
                def.isRoot(),
                null,
                def.toString()
        );
    }

    static ObjectInfo ofNull() {
        return new ObjectInfo(null, (a,b) -> Expression.simple("null"), false, null, "null");
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

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Integer getSplit() {
        return split;
    }

    public void setSplit(Integer split) {
        this.split = split;
    }
}
