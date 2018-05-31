package org.deletethis.hardcode.graph;

import org.deletethis.hardcode.objects.*;

import java.util.*;

public class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final Map<Object, Node> objectMap = new IdentityHashMap<>();
    private final Dag dag = new Dag();
    private final Set<Object> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

    public GraphBuilder(List<NodeFactory> nodeFactories) {
        this.nodeFactories = nodeFactories;
    }

    private static final ObjectInfo NULL = new ObjectInfo() {
        @Override
        public Class<?> getType() {
            return null;
        }

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
            return dag.createNode(NULL);
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
                Optional<NodeDefinition> nodeOptional = factory.createNode(o);
                if(nodeOptional.isPresent()) {
                    NodeDefinition nodeDef = nodeOptional.get();

                    Node node = dag.createNode(nodeDef.getObjectInfo());

                    for(Object param: nodeDef.getParameters()) {
                        Node n2 = createNode(param);
                        dag.createEdge(node, n2);
                    }

                    if(factory.enableReferenceDetection()) {
                        objectMap.put(o, node);
                    }
                    return node;
                }
            }
            throw new IllegalArgumentException();
        } finally {
            objectsInProgress.remove(o);
        }
    }

    public Dag buildGraph(Object o) {
        Node n = createNode(o);
        if(!objectsInProgress.isEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        dag.setRoot(n);
        return dag;
    }
}
