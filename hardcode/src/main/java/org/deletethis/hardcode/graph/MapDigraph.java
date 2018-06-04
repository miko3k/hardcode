package org.deletethis.hardcode.graph;

import java.util.*;

public class MapDigraph<T> implements Digraph<T> {
    private Set<VertexImpl> roots = new HashSet<>();
    private Set<Divertex<T>> rootsPublic = Collections.unmodifiableSet(roots);
    private Set<VertexImpl> allNodes = new HashSet<>();
    private Set<Divertex<T>> allNodesPublic = Collections.unmodifiableSet(allNodes);

    private class VertexImpl implements Divertex<T> {
        private final T payload;
        private final List<VertexImpl> successors = new ArrayList<>();
        private final List<Divertex<T>> successorsPublic = Collections.unmodifiableList(successors);
        /** same node may appear here multipe times, if it appears several times as a successor of the other node */
        private final List<VertexImpl> predecessors = new ArrayList<>();
        private final List<Divertex<T>> predecessorsPublic = Collections.unmodifiableList(predecessors);

        private VertexImpl(T payload) {
            this.payload = payload;
        }

        public T getPayload() {
            return payload;
        }

        public Collection<Divertex<T>> getSuccessors() {
            return successorsPublic;
        }

        void addPredecessor(VertexImpl node) {
            predecessors.add(node);
        }

        void addSuccessor(VertexImpl node) {
            successors.add(node);
        }

        @Override
        public Divertex<T> getSuccessor() {
            if(successors.size() != 1) {
                throw new IllegalStateException("successor count = " + successors.size());
            }
            return successors.get(0);
        }

        @Override
        public Divertex<T> getPredecessor() {
            if(predecessors.size() != 1) {
                throw new IllegalStateException("predecessor count = " + predecessors.size());
            }
            return predecessors.get(0);
        }

        @Override
        public String toString() {
            return payload.toString();
        }

        public Collection<Divertex<T>> getPredecessors() {
            return predecessorsPublic;
        }

        public int getInDegree() {
            return predecessors.size();
        }

        public int getOutDegree() {
            return successors.size();
        }

        public MapDigraph<T> getGraph() {
            return MapDigraph.this;
        }
    }

    public Collection<Divertex<T>> getRoots() {
        return rootsPublic;
    }

    private VertexImpl mine(Divertex<T> node) {
        Objects.requireNonNull(node, "Vertex is null");

        VertexImpl v = (VertexImpl) node;
        if(v.getGraph() != this)
            throw new IllegalArgumentException();

        return v;
    }

    public Divertex<T> createVertex(T payload) {
        VertexImpl n = new VertexImpl(payload);
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
        return allNodesPublic;
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
