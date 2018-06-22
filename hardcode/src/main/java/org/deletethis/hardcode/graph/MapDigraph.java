package org.deletethis.hardcode.graph;

import java.util.*;

public class MapDigraph<T,E> implements Digraph<T, E> {
    private Set<VertexImpl> roots = new HashSet<>();
    private Collection<T> rootsPublic = new AdapterCollection<>(roots, VertexImpl::getPayload);
    private Map<T, VertexImpl> allNodes = new HashMap<>();
    private Collection<T> allNodesPublic = new AdapterCollection<>(allNodes.values(), VertexImpl::getPayload);

    private class ConnectingEdge {
        private final E edge;
        private final VertexImpl vertex;

        public ConnectingEdge(E edge, VertexImpl vertex) {
            this.edge = edge;
            this.vertex = vertex;
        }

        public T payload() {
            return vertex.payload;
        }
    }

    private class VertexImpl {
        private final T payload;
        private final List<ConnectingEdge> successors = new ArrayList<>();
        private final Collection<T> successorsPublic = new AdapterCollection<>(successors, ConnectingEdge::payload);
        private final Collection<ConnectedVertex<T,E>> connectedSuccessorsPublic = new AdapterCollection<>(successors, connectingEdge -> new ConnectedVertex<T,E>() {
            @Override
            public T getVertex() {
                return connectingEdge.vertex.payload;
            }

            @Override
            public E getEdge() {
                return connectingEdge.edge;
            }
        });
        /** same node may appear here multipe times, if it appears several times as a successor of the other node */
        private final List<ConnectingEdge> predecessors = new ArrayList<>();
        private final Collection<T> predecessorsPublic = new AdapterCollection<>(predecessors, ConnectingEdge::payload);

        private VertexImpl(T payload) {
            this.payload = payload;
        }

        T getPayload() {
            return payload;
        }

        void addPredecessor(E edge, VertexImpl node) {
            predecessors.add(new ConnectingEdge(edge, node));
        }

        void addSuccessor(E edge, VertexImpl node) {
            successors.add(new ConnectingEdge(edge, node));
        }

        @Override
        public String toString() {
            return payload.toString();
        }

        int getInDegree() {
            return predecessors.size();
        }

        int getOutDegree() {
            return successors.size();
        }

        public MapDigraph<T,E> getGraph() {
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


    @Override
    public void createEdge(T from, T to, E edge) {
        VertexImpl f = mine(from);
        VertexImpl t = mine(to);

        f.addSuccessor(edge, t);
        t.addPredecessor(edge, f);

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
        return mine(vertex).successorsPublic;
    }
    public Collection<ConnectedVertex<T,E>> getSuccessorConnections(T vertex) {
        return mine(vertex).connectedSuccessorsPublic;
    }
    public Collection<T> getPredecessors(T vertex) {
        return mine(vertex).predecessorsPublic;
    }
    public int getInDegree(T vertex) {
        return mine(vertex).getInDegree();
    }
    public int getOutDegree(T vertex) {
        return mine(vertex).getOutDegree();
    }

}
