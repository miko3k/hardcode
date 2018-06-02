package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.Dag;
import org.deletethis.hardcode.graph.DagImpl;
import org.deletethis.hardcode.graph.Vertex;
import org.deletethis.hardcode.objects.*;

import java.util.*;

public class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final Map<Object, Vertex<ObjectInfo>> objectMap = new IdentityHashMap<>();
    private final Dag<ObjectInfo> dag = new DagImpl<>();
    private final Set<Object> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

    private GraphBuilder(List<NodeFactory> nodeFactories) {
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

    private Vertex<ObjectInfo> createNode(Object o) {
        //System.out.println("NODE: " + o);
        
        if(o == null) {
            return dag.createVertex(NULL);
        }

        if(!objectsInProgress.add(o)) {
            throw new IllegalStateException("cycle detected");
        }
        try {
            Vertex<ObjectInfo> n = objectMap.get(o);
            if (n != null) {
                return n;
            }

            for(NodeFactory factory: nodeFactories) {
                Optional<NodeDefinition> nodeOptional = factory.createNode(o);
                if(nodeOptional.isPresent()) {
                    NodeDefinition nodeDef = nodeOptional.get();

                    Vertex<ObjectInfo> node = dag.createVertex(nodeDef.getObjectInfo());

                    for(Object param: nodeDef.getParameters()) {
                        Vertex<ObjectInfo> n2 = createNode(param);
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

    public static Dag<ObjectInfo> buildGraph(List<NodeFactory> nodeFactories, Object o) {
        GraphBuilder gb = new GraphBuilder(nodeFactories);

        Vertex<ObjectInfo> n = gb.createNode(o);
        if(!gb.objectsInProgress.isEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        gb.dag.setRoot(n);
        return gb.dag;
    }
}
