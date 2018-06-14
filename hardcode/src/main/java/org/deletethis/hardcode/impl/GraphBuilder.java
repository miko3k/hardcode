package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.MapDigraph;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.objects.ObjectInfo;
import org.deletethis.hardcode.objects.*;

import java.lang.annotation.Annotation;
import java.util.*;

public class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final HardcodeConfiguration configuration;
    private final Map<Object, Divertex<ObjectInfo>> objectMap = new IdentityHashMap<>();
    private final Digraph<ObjectInfo> digraph = new MapDigraph<>();
    private final Set<Object> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

    private GraphBuilder(List<NodeFactory> nodeFactories, HardcodeConfiguration configuration) {
        this.nodeFactories = nodeFactories;
        this.configuration = configuration;
    }

    private static final ObjectInfo NULL = new ObjectInfo() {
        @Override
        public Class<?> getType() {
            return null;
        }

        @Override
        public Expression getCode(CodegenContext context, ObjectContext obj) {
            return Expression.simple("null");
        }

        @Override
        public boolean isRoot() {
            return false;
        }

        @Override
        public String toString() {
            return "[null]";
        }
    };

    private Divertex<ObjectInfo> createNode(Object o, List<Annotation> annotations) {
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

            BuiltinAnnotations ba = new BuiltinAnnotations();
            // this is never gonna be null, even if argument is
            List<Annotation> passedAnnotations = ba.process(annotations);

            for(NodeFactory factory: nodeFactories) {
                Optional<NodeDefinition> nodeOptional = factory.createNode(o, configuration, passedAnnotations);
                if(nodeOptional.isPresent()) {
                    NodeDefinition nodeDef = nodeOptional.get();

                    Divertex<ObjectInfo> node = digraph.createVertex(ba.wrap(nodeDef.getObjectInfo()));

                    for(NodeParameter param: nodeDef.getParameters()) {
                        Divertex<ObjectInfo> n2 = createNode(param.getValue(), param.getAnnotations());
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

    public static Digraph<ObjectInfo> buildGraph(List<NodeFactory> nodeFactories, HardcodeConfiguration configuration, Object o) {
        GraphBuilder gb = new GraphBuilder(nodeFactories, configuration);

        gb.createNode(o, null);
        if(!gb.objectsInProgress.isEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        return gb.digraph;
    }
}
