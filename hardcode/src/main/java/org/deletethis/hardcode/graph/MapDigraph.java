package org.deletethis.hardcode.graph;

import java.util.*;

public class MapDigraph<T> implements Digraph<T> {
    private Set<VertexImpl> roots = new HashSet<>();
    private Collection<T> rootsPublic = new AdapterCollection<>(roots, VertexImpl::getPayload);
    private Map<T, VertexImpl> allNodes = new HashMap<>();
    private Collection<T> allNodesPublic = new AdapterCollection<>(allNodes.values(), VertexImpl::getPayload);

    private class VertexImpl {
        private final T payload;
        private final List<VertexImpl> successors = new ArrayList<>();
        private final Collection<T> successorsPublic = new AdapterCollection<>(successors, VertexImpl::getPayload);
        /** same node may appear here multipe times, if it appears several times as a successor of the other node */
        private final List<VertexImpl> predecessors = new ArrayList<>();
        private final Collection<T> predecessorsPublic = new AdapterCollection<>(predecessors, VertexImpl::getPayload);

        private VertexImpl(T payload) {
            this.payload = payload;
        }

        T getPayload() {
            return payload;
        }

        Collection<T> getSuccessors() {
            return successorsPublic;
        }

        void addPredecessor(VertexImpl node) {
            predecessors.add(node);
        }

        void addSuccessor(VertexImpl node) {
            successors.add(node);
        }

        @Override
        public String toString() {
            return payload.toString();
        }

        Collection<T> getPredecessors() {
            return predecessorsPublic;
        }

        int getInDegree() {
            return predecessors.size();
        }

        int getOutDegree() {
            return successors.size();
        }

        public MapDigraph<T> getGraph() {
            return MapDigraph.this;
        }
    }

    public Collection<T> getRoots() {
        return rootsPublic;
    }

    private VertexImpl mine(T node) {
        Objects.requireNonNull(node, "Vertex is null");

        VertexImpl vertex = allNodes.get(node);

        if(vertex == null)
            throw new IllegalArgumentException();

        return vertex;
    }

    @Override
    public boolean containsEdge(T from, T to) {
        VertexImpl f = mine(from);
        VertexImpl t = mine(to);

        return f.successors.contains(t);
    }

    @Override
    public void addVertex(T vertex) {
        VertexImpl impl = new VertexImpl(vertex);

        VertexImpl prev = allNodes.putIfAbsent(vertex, impl);
        if(prev != null) {
            throw new IllegalArgumentException("duplicate vertex: " + vertex);
        }
        
        roots.add(impl);
    }


    public void createEdge(T from, T to) {
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
    public Collection<T> getAllVertices() {
        return allNodesPublic;
    }

    @Override
    public <V> Map<T, V> createMap() {
        return new HashMap<>();
    }

    @Override
    public Set<T> createSet() {
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

    public Collection<T> getSuccessors(T vertex) {
        return mine(vertex).getSuccessors();
    }
    public Collection<T> getPredecessors(T vertex) {
        return mine(vertex).getPredecessors();
    }
    public int getInDegree(T vertex) {
        return mine(vertex).getInDegree();
    }
    public int getOutDegree(T vertex) {
        return mine(vertex).getOutDegree();
    }

}
