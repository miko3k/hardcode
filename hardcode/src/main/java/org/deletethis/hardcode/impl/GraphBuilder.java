package org.deletethis.hardcode.impl;

import org.deletethis.graph.Digraph;
import org.deletethis.graph.MapDigraph;
import org.deletethis.graph.Divertex;
import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.objects.*;

import java.util.*;

public class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final Map<Object, Divertex<ObjectInfo>> objectMap = new IdentityHashMap<>();
    private final Digraph<ObjectInfo> digraph = new MapDigraph<>();
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

    private Divertex<ObjectInfo> createNode(Object o) {
        //System.out.println("NODE: " + o);
        
        if(o == null) {
            return digraph.createVertex(NULL);
        }

        if(!objectsInProgress.add(o)) {
            throw new IllegalStateException("cycle detected");
        }
        try {
            Divertex<ObjectInfo> n = objectMap.get(o);
            if (n != null) {
                return n;
            }

            for(NodeFactory factory: nodeFactories) {
                Optional<NodeDefinition> nodeOptional = factory.createNode(o);
                if(nodeOptional.isPresent()) {
                    NodeDefinition nodeDef = nodeOptional.get();

                    Divertex<ObjectInfo> node = digraph.createVertex(nodeDef.getObjectInfo());

                    for(Object param: nodeDef.getParameters()) {
                        Divertex<ObjectInfo> n2 = createNode(param);
                        digraph.createEdge(node, n2);
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

    public static Digraph<ObjectInfo> buildGraph(List<NodeFactory> nodeFactories, Object o) {
        GraphBuilder gb = new GraphBuilder(nodeFactories);

        gb.createNode(o);
        if(!gb.objectsInProgress.isEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        return gb.digraph;
    }
}