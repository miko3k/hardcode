package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.CycleException;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.MapDigraph;
import org.deletethis.hardcode.objects.*;

import java.lang.annotation.Annotation;
import java.util.*;

public class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final HardcodeConfiguration configuration;
    private final Map<Object, ObjectInfo> objectMap = new IdentityHashMap<>();
    private final Digraph<ObjectInfo, ParameterName> digraph = new MapDigraph<>();
    private final Set<Object> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

    private GraphBuilder(List<NodeFactory> nodeFactories, HardcodeConfiguration configuration) {
        this.nodeFactories = nodeFactories;
        this.configuration = configuration;
    }

    private ObjectInfo createNode(Object object, List<Annotation> annotations) {
        //System.out.println("NODE: " + o);

        if(object == null) {
            ObjectInfo nullObject = ObjectInfo.ofNull();
            digraph.addVertex(nullObject);
            return nullObject;
        }

        if(!objectsInProgress.add(object)) {
            throw new CycleException("cycle detected");
        }

        try {
            BuiltinAnnotations ba = new BuiltinAnnotations(annotations);

            ObjectInfo n = objectMap.get(object);
            if (n != null) {
                ba.apply(n);
                return n;
            }

            for(NodeFactory factory: nodeFactories) {
                Optional<NodeDefinition> nodeOptional = factory.createNode(object, configuration);
                if(nodeOptional.isPresent()) {
                    NodeDefinition nodeDef = nodeOptional.get();

                    ObjectInfo node = ObjectInfo.ofNodeDefinion(nodeDef);
                    ba.apply(node);
                    digraph.addVertex(node);

                    for(NodeParameter param: nodeDef.getParameters()) {
                        ObjectInfo otherNode = createNode(param.getValue(), param.getAnnotations());
                        digraph.createEdge(node, otherNode, param.getParameterName());
                    }

                    if(factory.enableReferenceDetection()) {
                        objectMap.put(object, node);
                    }
                    return node;
                }
            }
            throw new IllegalArgumentException();
        } finally {
            objectsInProgress.remove(object);
        }
    }

    public static Digraph<ObjectInfo, ParameterName> buildGraph(List<NodeFactory> nodeFactories, HardcodeConfiguration configuration, Object o) {
        GraphBuilder gb = new GraphBuilder(nodeFactories, configuration);

        gb.createNode(o, null);
        if(!gb.objectsInProgress.isEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        new GraphVerifier(gb.digraph).verify();
        return gb.digraph;
    }
}
