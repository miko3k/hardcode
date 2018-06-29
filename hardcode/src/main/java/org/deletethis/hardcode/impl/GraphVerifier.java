package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.*;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.MapDigraph;
import org.deletethis.hardcode.objects.NodeDefinition;
import org.deletethis.hardcode.objects.NodeFactory;
import org.deletethis.hardcode.objects.NodeParameter;
import org.deletethis.hardcode.objects.ParameterName;

import java.lang.annotation.Annotation;
import java.util.*;

class GraphVerifier {
    private final Digraph<ObjectInfo, ParameterName> digraph;
    private final Map<ObjectInfo, ObjectInfo> parents;

    public GraphVerifier(Digraph<ObjectInfo, ParameterName> digraph) {
        this.digraph = digraph;
        this.parents = digraph.createMap();
    }

    private void dfs(ObjectInfo currentVertex, ObjectInfo currentRoot) {
        ObjectInfo root = parents.get(currentVertex);
        if(root != null) {
            if(root != currentRoot) {
                throw new CrossRootReferenceException("cross-root reference");
            }
            return;
        }
        parents.put(currentVertex, currentRoot);
        for(ObjectInfo successor: digraph.getSuccessors(currentVertex)) {
            if(currentVertex.isRoot()) {
                dfs(successor, currentVertex);
            } else if(currentVertex.getSplit() != null) {
                if(digraph.getInDegree(successor) != 1) {
                    throw new CrossRootReferenceException("Each member of @" + HardcodeSplit.class.getSimpleName() + " must be referenced once");
                }
                dfs(successor, successor);
            } else {
                dfs(successor, currentRoot);
            }
        }
    }

    public void verify() {
        ObjectInfo root = digraph.getRoot();

        dfs(root, root);
    }
}
