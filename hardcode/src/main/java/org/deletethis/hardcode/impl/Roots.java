package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.*;

import java.util.*;

public class Roots {

    private void createNodes(Set<Divertex<ObjectInfo>> visited, Divertex<ObjectInfo> current) {
        if(visited.contains(current))
            return;

        visited.add(current);

        if(current.getPayload().isRoot()) {
            Divertex<Divertex<ObjectInfo>> vertex = rootGraph.createVertex(current);
            theMap.put(current, vertex);
        }

        for(Divertex<ObjectInfo> v: current.getSuccessors()) {
            createNodes(visited, v);
        }
    }

    private void createEdges(Set<Divertex<ObjectInfo>> visited, Divertex<Divertex<ObjectInfo>> currentRoot, Divertex<ObjectInfo> current) {
        if(visited.contains(current))
            return;

        visited.add(current);
        if(current.getPayload().isRoot() && currentRoot.getPayload() != current) {
            Divertex<Divertex<ObjectInfo>> otherRoot = Objects.requireNonNull(theMap.get(current));
            if(!rootGraph.containsEdge(currentRoot, otherRoot))
                rootGraph.createEdge(currentRoot, otherRoot);

            // no not visit pay this vertex
        } else {
            for(Divertex<ObjectInfo> v: current.getSuccessors()) {
                createEdges(visited, currentRoot, v);
            }
        }
    }

    private void dfs(Set<Divertex<Divertex<ObjectInfo>>> visited, Divertex<Divertex<ObjectInfo>> current) {
        if(visited.contains(current))
            return;

        visited.add(current);
        for(Divertex<Divertex<ObjectInfo>> v: current.getSuccessors()) {
            dfs(visited, v);
        }
    }

    private final Digraph<Divertex<ObjectInfo>> rootGraph = new MapDigraph<>();
    private final Map<Divertex<ObjectInfo>, Divertex<Divertex<ObjectInfo>>> theMap;
    private final List<Root> theList;

    private Roots(Digraph<ObjectInfo> graph, NumberNameAllocator nameAllocator) {
        theMap = graph.createMap();

        Set<Divertex<ObjectInfo>> visited = graph.createSet();
        createNodes(visited, graph.getRoot());

        for(Divertex<Divertex<ObjectInfo>> root: rootGraph.getAllVertices()) {
            Set<Divertex<ObjectInfo>> done = graph.createSet();
            createEdges(done, root, root.getPayload());
        }

        List<Root> roots = new ArrayList<>();
        for(Divertex<Divertex<ObjectInfo>> root: TopoSort.topologicalSort(rootGraph)) {
            List<Divertex<ObjectInfo>> dependencies = new ArrayList<>();
            for(Divertex<Divertex<ObjectInfo>> v: root.getSuccessors()) {
                dependencies.add(v.getPayload());
            }
            Divertex<ObjectInfo> vertex = root.getPayload();
            Class<?> returnType = vertex.getPayload().getType();
            String methodName = nameAllocator.newName("create" + returnType.getSimpleName());

            dependencies = Collections.unmodifiableList(dependencies);
            roots.add(new Root(vertex, methodName, dependencies));
        }
        Collections.reverse(roots);
        theList = Collections.unmodifiableList(roots);
    }

    static List<Root> getRoots(NumberNameAllocator nameAllocator, Digraph<ObjectInfo> graph) {
        return new Roots(graph, nameAllocator).theList;
    }
}
