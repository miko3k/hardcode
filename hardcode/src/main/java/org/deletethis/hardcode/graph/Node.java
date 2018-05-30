package org.deletethis.hardcode.graph;

import org.deletethis.hardcode.objects.ConstructionStrategy;
import org.deletethis.hardcode.objects.NodeDefinition;

import java.util.*;

public class Node implements NodeDefinition {
    private final ConstructionStrategy constructor;
    private final Class<?> type;
    private final List<Node> parameters;
    /** same not may appear here multipe times, if it appears several times as a paramter of other node */
    private final List<Node> users = new ArrayList<>();

    Node(Class<?> type, List<Node> parameters, ConstructionStrategy constructor) {
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

    public void addUser(Node node) {
        users.add(node);
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append(System.identityHashCode(this));
        bld.append("[");
        bld.append(users.size());
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
        return users.size();
    }
}
