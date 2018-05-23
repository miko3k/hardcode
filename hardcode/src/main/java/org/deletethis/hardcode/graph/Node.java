package org.deletethis.hardcode.graph;

import org.deletethis.hardcode.ConstructionStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Node {
    private final ConstructionStrategy constructor;
    private final Class<?> type;
    private final List<Node> parameters;
    private int refCount = 0;

    public Node(Class<?> type, List<Node> parameters, ConstructionStrategy constructor) {
        this.constructor = Objects.requireNonNull(constructor);
        this.type = type;
        this.parameters = Objects.requireNonNull(parameters);
    }

    public Node(Class<?> type, ConstructionStrategy constructor) {
        this(type, Collections.emptyList(), constructor);
    }

    public ConstructionStrategy getConstructor() {
        return constructor;
    }

    public List<Node> getParameters() {
        return parameters;
    }

    public Class<?> getType() {
        return type;
    }

    public void increateRefCount() {
        ++refCount;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append(System.identityHashCode(this));
        bld.append("[");
        bld.append(refCount);
        bld.append("]");
        bld.append(": ");
        bld.append(constructor);

        boolean first = true;
        for(Node n: parameters) {
            if(first) {
                bld.append("(");
                first = false;
            } else {
                bld.append(",");
            }
            bld.append(System.identityHashCode(n));
        }
        if(!first)
            bld.append(")");

        return bld.toString();
    }

    public int getRefCount() {
        return refCount;
    }
}
