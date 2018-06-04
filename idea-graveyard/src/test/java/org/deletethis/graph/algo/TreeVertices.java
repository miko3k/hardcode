package org.deletethis.graph.algo;

import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Divertex;

import java.util.Set;

/**
 * A brute force algorithm to find "tree vertices"
 *
 *   <https://stackoverflow.com/questions/50657404/finding-all-tree-like-vertices-in-a-dag>
 *
 * Still in search of a better algorithm.
 *
 */
class TreeVertices<T> {
    private final Digraph<T> graph;

    TreeVertices(Digraph<T> graph) {
        this.graph = graph;
    }

    private boolean isAnySuccessorReachable(Set<Divertex<T>> visited, Divertex<T> mainVertex, Divertex<T> current) {
        if(current == mainVertex) {
            return false;
        }
        if(visited.contains(current)) {
            return false;
        }
        if(mainVertex.getSuccessors().contains(current)) {
            return true;
        }
        visited.add(current);
        for(Divertex<T> v: current.getSuccessors()) {
            if(isAnySuccessorReachable(visited, mainVertex, v)) {
                return true;
            }
        }
        for(Divertex<T> v: current.getPredecessors()) {
            if(isAnySuccessorReachable(visited, mainVertex, v)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTreeVertex(Divertex<T> vertex) {
        Set<Divertex<T>> visited = graph.createSet();
        if(vertex.getOutDegree() == 0) {
            return false;
        } else {
            return !isAnySuccessorReachable(visited, vertex, graph.getRoots().iterator().next());
        }
    }

    Set<Divertex<T>> run() {
        Set<Divertex<T>> out = graph.createSet();
        for(Divertex<T> v: graph.getAllVertices()) {
            System.out.println("V: " + v);

            if(isTreeVertex(v)) {
                out.add(v);
            }
        }
        return out;

    }
}
