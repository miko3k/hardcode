package org.deletethis.hardcode.graph;

import java.util.*;

public class TopoSort<T> implements Iterator<Divertex<T>> {
    private final Digraph<T> digraph;
    private final Map<Divertex<T>, Set<Divertex<T>>> removedEdges;
    private final ArrayDeque<Divertex<T>> s;
    // mark vertices which we discovered as successors - just to detect cycles
    private final Set<Divertex<T>> discovered;
    // count vertices which we returned, must be equal to discovered.size()
    private int returned = 0;

    private void removeEdge(Divertex<T> from, Divertex<T> to) {
        Set<Divertex<T>> set = removedEdges.get(from);
        if (set == null) {
            set = digraph.createSet();
            removedEdges.put(from, set);
        }
        set.add(to);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEdgeRemoved(Divertex<T> from, Divertex<T> to) {
        Set<Divertex<T>> set = removedEdges.get(from);
        if (set != null) {
            return set.contains(to);
        } else {
            return false;
        }
    }

    private boolean hasAnyPredecessor(Divertex<T> m) {
        for (Divertex<T> t : m.getPredecessors()) {
            if (!isEdgeRemoved(t, m)) {
                return true;
            }
        }
        return false;
    }


    TopoSort(Digraph<T> input, Collection<Divertex<T>> roots) {
        this.digraph = input;
        this.s = new ArrayDeque<>(32);
        this.s.addAll(roots);
        this.removedEdges = digraph.createMap();
        this.discovered = digraph.createSet();
        this.discovered.addAll(roots);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        return !s.isEmpty();
    }

    @Override
    public Divertex<T> next() {
        /**
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
        Divertex<T> result = s.pollFirst();
        if(result == null) {
            throw new NoSuchElementException("Already reached the last vertex");
        }

        for(Divertex<T> m : result.getSuccessors()) {
            discovered.add(m);
            removeEdge(result, m);
            if(!hasAnyPredecessor(m)) {
                s.addLast(m);
            }
        }
        ++returned;
        if(s.isEmpty()) {
            if (discovered.size() != returned) {
                throw new IllegalStateException("cycle detected! discovered " + discovered.size() + ", returned = " + returned);
            }
        }
        return result;
    }

    public static <T> Iterable<Divertex<T>> topologicalSort(Digraph<T> graph) {
        return () -> new TopoSort<>(graph, graph.getRoots());
    }

    public static <T> Iterable<Divertex<T>> topologicalSort(Divertex<T> vertex) {
        return () -> new TopoSort<>(vertex.getGraph(), Collections.singletonList(vertex));
    }

}
