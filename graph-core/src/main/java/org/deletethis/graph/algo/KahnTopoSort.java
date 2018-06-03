package org.deletethis.graph.algo;

import org.deletethis.graph.Digraph;
import org.deletethis.graph.Divertex;

import java.util.*;

class KahnTopoSort {
    private static class RemovedEdges<T> {
        private final Digraph<T> digraph;
        private final Map<Divertex<T>, Set<Divertex<T>>> removedEdges;

        private RemovedEdges(Digraph<T> digraph) {
            this.digraph = digraph;
            this.removedEdges = digraph.createMap();
        }

        private void add(Divertex<T> from, Divertex<T> to) {
            Set<Divertex<T>> set = removedEdges.get(from);
            if(set == null) {
                set = digraph.createSet();
                removedEdges.put(from, set);
            }
            set.add(to);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean contains(Divertex<T> from, Divertex<T> to) {
            Set<Divertex<T>> set = removedEdges.get(from);
            if(set != null) {
                return set.contains(to);
            } else {
                return false;
            }
        }

        private boolean hasAnyPredecessor(Divertex<T> m) {
            for(Divertex<T> t: m.getPredecessors()) {
                if(!contains(t, m)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Performs a topological sorting.
     *
     * <https://en.wikipedia.org/wiki/Topological_sorting>
     *
     * L ← Empty list that will contain the sorted elements
     * S ← Set of all nodes with no incoming edge
     *
     * while S is non-empty do
     *     remove a node n from S
     *     add n to tail of L
     *     for each node m with an edge e from n to m do
     *         remove edge e from the graph
     *         if m has no other incoming edges then
     *             insert m into S
     * if graph has edges then
     *     return error (graph has at least one cycle)
     * else
     *     return L (a topologically sorted order)
     */
    static <T> List<Divertex<T>> run(Digraph<T> input) {
        List<Divertex<T>> output = new ArrayList<>(input.getAllVertices().size());
        ArrayList<Divertex<T>> s = new ArrayList<>(input.getRoots());
        RemovedEdges<T> removedEdges = new RemovedEdges<>(input);

        while(!s.isEmpty()) {
            Iterator<Divertex<T>> iterator = s.iterator();
            Divertex<T> n = iterator.next();
            iterator.remove();

            output.add(n);
            System.out.println("-- "  +n);

            for(Divertex<T> m: n.getSuccessors()) {
                removedEdges.add(n, m);
                if(!removedEdges.hasAnyPredecessor(m)) {
                    s.add(m);
                }
            }
        }

        for(Divertex<T> v: input.getAllVertices()) {
            for(Divertex<T> w: v.getSuccessors()) {
                if(!removedEdges.contains(v, w)) {
                    throw new IllegalStateException("topological sort failed, graph contains cycles");
                }
            }
        }

        return output;
    }
}
