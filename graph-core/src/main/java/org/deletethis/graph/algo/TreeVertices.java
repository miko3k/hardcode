package org.deletethis.graph.algo;

import org.deletethis.graph.Digraph;
import org.deletethis.graph.Divertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class TreeVertices<T> {
    private final Digraph<T> graph;

    TreeVertices(Digraph<T> graph) {
        this.graph = graph;
    }

    /**
     * A brute force algorithm to find "tree vertices"
     *
     *   <https://stackoverflow.com/questions/50657404/finding-all-tree-like-vertices-in-a-dag>
     *
     * Still in search of a better algorithm.
     *
     * Brute force approach would involve removing a vertex, and then checking if any successor
     * is still reachable (ignoring orientation). This algorithm is a slight improvement byt splitting
     * graph into tree separate trees and processing them one by one
     *
     * This one works as following:
     *
     *    find(root) -> [vertices]
     *        out = []
     *
     *        for v in all vertices under root in topological order
     *            remove remove vertices out + [v] (for this iteration only)
     *            run DFS, ignoring orientation
     *            if any successor of v was reached
     *                out += v
     *
     *            if v was not reached
     *                break
     *
     *        result = out
     *        for v in out:
     *            result += find(v)
     *
     *        return result
     *
     * DFS prefers predecessors for performance (NEED VERIFICATION IF IT'S ACTUALLY FASTER)
     *
     */
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
