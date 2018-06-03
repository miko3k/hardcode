package org.deletethis.graph;

import java.util.*;

public class MapDigraph<T> implements Digraph<T> {
    private Set<Divertex<T>> roots = new HashSet<>();
    private Set<Divertex<T>> allNodes = new HashSet<>();

    private class VertexImpl implements Divertex<T> {
        private final T payload;
        private final List<VertexImpl> successors = new ArrayList<>();
        /** same node may appear here multipe times, if it appears several times as a successor of the other node */
        private final List<VertexImpl> predecessors = new ArrayList<>();

        private VertexImpl(T payload) {
            this.payload = payload;
        }

        public T getPayload() {
            return payload;
        }

        public Collection<Divertex<T>> getSuccessors() {
            return Collections.unmodifiableList(successors);
        }

        void addPredecessor(VertexImpl node) {
            predecessors.add(node);
        }

        void addSuccessor(VertexImpl node) {
            successors.add(node);
        }

        @Override
        public String toString() {
            StringBuilder bld = new StringBuilder();
            bld.append(System.identityHashCode(this));
            bld.append("[");
            bld.append(predecessors.size());
            bld.append("]");
            bld.append(": ");
            bld.append(payload);

            boolean first = true;
            for(VertexImpl n: successors) {
                if(first) {
                    bld.append("(");
                    first = false;
                } else {
                    bld.append(",");
                }
                bld.append(System.identityHashCode(n));
            }
            if(!first)
                bld.append(")");

            return bld.toString();
        }

        public Collection<Divertex<T>> getPredecessors() {
            return Collections.unmodifiableList(predecessors);
        }

        public int getInDegree() {
            return predecessors.size();
        }

        public int getOutDegree() {
            return successors.size();
        }

        public MapDigraph getGraph() {
            return MapDigraph.this;
        }
    }

    public Collection<Divertex<T>> getRoots() {
        return Collections.unmodifiableSet(roots);
    }

    private VertexImpl mine(Divertex<T> node) {
        Objects.requireNonNull(node, "Vertex is null");

        VertexImpl v = (VertexImpl) node;
        if(v.getGraph() != this)
            throw new IllegalArgumentException();

        return v;
    }

    public Divertex<T> createVertex(T payload) {
        Divertex<T> n = new VertexImpl(payload);
        allNodes.add(n);
        roots.add(n);
        return n;
    }

    @Override
    public boolean containsEdge(Divertex<T> from, Divertex<T> to) {
        VertexImpl f = mine(from);
        VertexImpl t = mine(to);

        return f.successors.contains(t);
    }

    public void createEdge(Divertex<T> from, Divertex<T> to) {
        VertexImpl f = mine(from);
        VertexImpl t = mine(to);

        f.addSuccessor(t);
        t.addPredecessor(f);

        roots.remove(t);
        if(f.predecessors.isEmpty()) {
            roots.add(f);
        } else {
            roots.remove(f);
        }
    }

    @Override
    public Collection<Divertex<T>> getAllVertices() {
        return Collections.unmodifiableCollection(allNodes);
    }

    @Override
    public <V> Map<Divertex<T>, V> createMap() {
        return new HashMap<>();
    }

    @Override
    public Set<Divertex<T>> createSet() {
        return new HashSet<>();
    }

    @Override
    public boolean isEmpty() {
        return allNodes.isEmpty();
    }

    @Override
    public boolean isOriented() {
        return true;
    }
}
