package org.deletethis.hardcode.graph;

import java.util.*;

public class DagImpl<T> implements Dag<T> {
    private VertexImpl root = null;
    private Set<Vertex<T>> allNodes = new HashSet<>();

    private class VertexImpl implements Vertex<T> {
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

        public Collection<Vertex<T>> getSuccessors() {
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

        public Collection<Vertex<T>> getPredecessors() {
            return Collections.unmodifiableList(predecessors);
        }

        public int getInDegree() {
            return predecessors.size();
        }

        public int getOutDegree() {
            return successors.size();
        }

        public DagImpl getGraph() {
            return DagImpl.this;
        }
    }

    public Vertex<T> getRoot() {
        return root;
    }

    private VertexImpl mine(Vertex<T> node) {
        VertexImpl v = (VertexImpl) node;
        if(v.getGraph() != this)
            throw new IllegalArgumentException();

        return v;
    }

    public void setRoot(Vertex<T> node) {
        if(node.getInDegree() != 0) {
            throw new IllegalArgumentException();
        }
        root = mine(node);
    }

    public Vertex<T> createVertex(T objectInfo) {
        Vertex<T> n = new VertexImpl(objectInfo);
        allNodes.add(n);
        return n;
    }

    public void createEdge(Vertex<T> from, Vertex<T> to) {
        VertexImpl f = mine(from);
        VertexImpl t = mine(to);

        f.addSuccessor(t);
        t.addPredecessor(f);
    }

    @Override
    public Collection<Vertex<T>> getAllVertices() {
        return Collections.unmodifiableCollection(allNodes);
    }

    @Override
    public boolean isEmpty() {
        return allNodes.isEmpty();
    }
}
