package org.deletethis.hardcode.graph;

import java.util.*;

public class TopoSort<T,E> implements Iterator<T> {
    private final Digraph<T,E> digraph;
    private final Map<T, Set<T>> removedEdges;
    private final ArrayDeque<T> s;
    // mark vertices which we discovered as successors - just to detect cycles
    private final Set<T> discovered;
    // count vertices which we returned, must be equal to discovered.size()
    private int returned = 0;

    private void removeEdge(T from, T to) {
        Set<T> set = removedEdges.get(from);
        if (set == null) {
            set = digraph.createSet();
            removedEdges.put(from, set);
        }
        set.add(to);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEdgeRemoved(T from, T to) {
        Set<T> set = removedEdges.get(from);
        if (set != null) {
            return set.contains(to);
        } else {
            return false;
        }
    }

    private boolean hasAnyPredecessor(T m) {
        for (T t : digraph.getPredecessors(m)) {
            if (!isEdgeRemoved(t, m)) {
                return true;
            }
        }
        return false;
    }


    TopoSort(Digraph<T,E> input, Collection<T> roots) {
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
    public T next() {
        /*
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
        T result = s.pollFirst();
        if(result == null) {
            throw new NoSuchElementException("Already reached the last vertex");
        }

        for(T m : digraph.getSuccessors(result)) {
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

    public static <T,E> Iterable<T> topologicalSort(Digraph<T,E> graph) {
        return () -> new TopoSort<>(graph, graph.getRoots());
    }
}
