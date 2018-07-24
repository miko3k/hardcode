package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.*;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.MapDigraph;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.util.TypeUtil;

import java.util.*;

class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final HardcodeConfiguration configuration;
    private final Map<Object, ObjectInfoImpl> objectMap = new IdentityHashMap<>();
    private final Digraph<ObjectInfo, ParameterName> digraph = new MapDigraph<>();
    private final Set<Object> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

    GraphBuilder(List<NodeFactory> nodeFactories, HardcodeConfiguration configuration) {
        this.nodeFactories = nodeFactories;
        this.configuration = configuration;
    }

    private void applyAnnotations(ObjectInfoImpl n, BuiltinAnnotations annotations) {
        if(annotations.isRoot())
            n.makeRoot();

        if(annotations.getSplit() != null)
            n.setSplit(annotations.getSplit());
    }


    private void applyClass(ObjectInfoImpl node, Class<?> clz) {
        boolean root = false;
        for(Class<?> c: TypeUtil.ancestors(clz)) {
            if(configuration.isRootClass(c)) {
                root = true;
            }

            if(c.isAnnotationPresent(HardcodeRoot.class)) {
                root = true;
            }
        }
        if(root) {
            node.makeRoot();
        }
    }

    private BuiltinAnnotations defaultAnnotations(Class<?> clz, ParameterName param) {
        BuiltinAnnotations result = new BuiltinAnnotations();
        for(Class<?> c: TypeUtil.ancestors(clz)) {
            if(configuration.isRootMembers(c, param)) {
                result.makeRoot();
            }
            Integer split = configuration.getSplitMember(clz, param);
            if(split != null && result.getSplit() != null) {
                // only set split if it's not set yet. This allow setting different values in a subclasses
                result.setSplit(split);
            }
        }
        return result;
    }

    private NodeDefinition createNodeDefinition(Object object) {
        for(NodeFactory factory: nodeFactories) {
            Optional<NodeDefinition> tmp = factory.createNode(object, configuration);
            if(tmp.isPresent()) {
                return tmp.get();
            }
        }
        throw new HardcodeException("unhandled type of class: " + object.getClass());
    }

    ObjectInfo createNode(Object object, BuiltinAnnotations annotations) {

        if(object == null) {
            ObjectInfo nullObject = new ObjectInfoNull();
            digraph.addVertex(nullObject);
            return nullObject;
        }

        if(!objectsInProgress.add(object)) {
            throw new CycleException("cycle detected");
        }

        try {
            ObjectInfoImpl n = objectMap.get(object);

            if(n != null) {
                applyAnnotations(n, annotations);
                return n;
            }

            NodeDefinition nodeDef = createNodeDefinition(object);

            ObjectInfoImpl node = new ObjectInfoImpl(nodeDef);

            applyAnnotations(node, annotations);
            applyClass(node, node.getType());

            digraph.addVertex(node);

            for (NodeParameter param : nodeDef.getParameters()) {
                BuiltinAnnotations builtinAnnotations = defaultAnnotations(node.getType(), param.getName());
                builtinAnnotations.addAnnotations(param.getAnnotations());

                ObjectInfo otherNode = createNode(param.getValue(), builtinAnnotations);
                digraph.createEdge(node, otherNode, param.getName());
            }

            if (!nodeDef.isValueBased()) {
                objectMap.put(object, node);
            }
            return node;

        } finally {
            objectsInProgress.remove(object);
        }
    }

    boolean isInProgressEmpty() {
        return objectsInProgress.isEmpty();
    }

    Digraph<ObjectInfo, ParameterName> getGraph() {
        return digraph;
    }
}
