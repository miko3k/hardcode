package org.deletethis.hardcode.graph;

import org.deletethis.hardcode.objects.*;

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

            NodeFactoryContext ctx = new NodeFactoryContext() {
                @Override
                public NodeDefinition getNode(Object object) {
                    return GraphBuilder.this.createNode(object);
                }

                @SuppressWarnings("unchecked")
                public NodeDefinition createNode(Class<?> type, List<NodeDefinition> parameters, ConstructionStrategy constructor) {
                    // super hacky cast, but should work, there's no other class implementing {@link NodeDefinition} iterface
                    return new Node(type, (List<Node>)(List<?>)(parameters), constructor);
                }
            };

            for(NodeFactory factory: nodeFactories) {
                Optional<NodeDefinition> nodeOptional = factory.createNode(ctx, o);
                if(nodeOptional.isPresent()) {
                    Node node = (Node)nodeOptional.get();
                    if(factory.enableReferenceDetection()) {
                        objectMap.put(o, node);
                    }
                    for(Node param: node.getParameters()) {
                        param.addUser(node);
                    }
                    allNodes.add(node);
                    return node;
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
