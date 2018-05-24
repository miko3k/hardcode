package org.deletethis.hardcode.graph;

import org.deletethis.hardcode.codegen.CodegenContext;
import org.deletethis.hardcode.codegen.ConstructionStrategy;
import org.deletethis.hardcode.codegen.Expression;

import java.util.*;

public class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final Map<Object, Node> objectMap = new IdentityHashMap<>();
    private final Set<Node> allNodes = new HashSet<>();
    private final Set<Object> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

    public GraphBuilder(List<NodeFactory> nodeFactories) {
        this.nodeFactories = nodeFactories;
    }

    private static final ConstructionStrategy NULL = new ConstructionStrategy() {
        @Override
        public Expression getCode(CodegenContext context, List<Expression> arguments) {
            return Expression.simple("null");
        }

        @Override
        public String toString() {
            return "null";
        }
    };

    public Node createNode(Object o) {
        //System.out.println("NODE: " + o);
        
        if(o == null) {
            Node n = new Node(null, NULL);
            allNodes.add(n);
            return n;
        }

        if(!objectsInProgress.add(o)) {
            throw new IllegalStateException("cycle detected");
        }
        try {
            Node n = objectMap.get(o);
            if (n != null) {
                return n;
            }

            for(NodeFactory factory: nodeFactories) {
                Optional<Node> node = factory.createNode(this::createNode, o);
                if(node.isPresent()) {
                    n = node.get();
                    if(factory.enableReferenceDetection()) {
                        objectMap.put(o, n);
                    }
                    for(Node param: n.getParameters()) {
                        param.increateRefCount();
                    }
                    allNodes.add(n);
                    return n;
                }
            }
            throw new IllegalArgumentException();
        } finally {
            objectsInProgress.remove(o);
        }
    }

    public Graph buildGraph(Object o) {
        Node n = createNode(o);
        if(!objectsInProgress.isEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        return new Graph(n, allNodes);
    }
}
