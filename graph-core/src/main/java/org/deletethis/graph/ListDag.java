package org.deletethis.graph;

import java.util.*;

public class ListDag<T> implements Dag<T> {
    private class VertexImpl implements DiVertex<T> {
        private final int number;
        private T payload;
        private List<Integer> successors = new ArrayList<>();
        private List<Integer> predecessors = new ArrayList<>();

        public VertexImpl(int number, T payload) {
            this.number = number;
            this.payload = payload;
        }

        @Override
        public T getPayload() {
            return payload;
        }

        @Override
        public Collection<DiVertex<T>> getSuccessors() {
            return null;
        }

        @Override
        public Collection<DiVertex<T>> getPredecessors() {
            return null;
        }

        @Override
        public int getInDegree() {
            return 0;
        }

        @Override
        public int getOutDegree() {
            return 0;
        }

        public ListDag<T> getGraph() {
            return ListDag.this;
        }
    }

    private int root;
    private List<VertexImpl> vertices = new ArrayList<>();


    @Override
    public DiVertex<T> getRoot() {
        if(root < 0) {
            return null;
        } else {
            return vertices.get(root);
        }
    }

    private VertexImpl myVertex(DiVertex<T> v) {
        VertexImpl impl = (VertexImpl)v;
        if(impl.getGraph() != this) {
            throw new IllegalArgumentException("different graph");
        }
        return impl;
    }

    @Override
    public void setRoot(DiVertex<T> node) {
        this.root = myVertex(node).number;
    }

    @Override
    public DiVertex<T> createVertex(T objectInfo) {
        return null;
    }

    @Override
    public void createEdge(DiVertex<T> from, DiVertex<T> to) {

    }

    @Override
    public Collection<DiVertex<T>> getAllVertices() {
        return null;
    }

    @Override
    public Set<DiVertex<T>> createSet() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isOriented() {
        return true;
    }

    @Override
    public <V> Map<DiVertex<T>, V> createMap() {
        return null;
    }
}
