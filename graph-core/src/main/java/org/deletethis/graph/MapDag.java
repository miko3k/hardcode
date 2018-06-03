package org.deletethis.graph;

import java.util.*;
import java.util.function.Supplier;

public class MapDag<T> implements Dag<T> {
    private VertexImpl root = null;
    private Set<DiVertex<T>> allNodes = new HashSet<>();

    public static <X> Supplier<MapDag<X>> supplier() {
        return MapDag::new;
    }

    private class VertexImpl implements DiVertex<T> {
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

        public Collection<DiVertex<T>> getSuccessors() {
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

        public Collection<DiVertex<T>> getPredecessors() {
            return Collections.unmodifiableList(predecessors);
        }

        public int getInDegree() {
            return predecessors.size();
        }

        public int getOutDegree() {
            return successors.size();
        }

        public MapDag getGraph() {
            return MapDag.this;
        }
    }

    public DiVertex<T> getRoot() {
        return root;
    }

    private VertexImpl mine(DiVertex<T> node) {
        VertexImpl v = (VertexImpl) node;
        if(v.getGraph() != this)
            throw new IllegalArgumentException();

        return v;
    }

    public void setRoot(DiVertex<T> node) {
        if(node.getInDegree() != 0) {
            throw new IllegalArgumentException();
        }
        root = mine(node);
    }

    public DiVertex<T> createVertex(T objectInfo) {
        DiVertex<T> n = new VertexImpl(objectInfo);
        allNodes.add(n);
        return n;
    }

    public void createEdge(DiVertex<T> from, DiVertex<T> to) {
        VertexImpl f = mine(from);
        VertexImpl t = mine(to);

        f.addSuccessor(t);
        t.addPredecessor(f);
    }

    @Override
    public Collection<DiVertex<T>> getAllVertices() {
        return Collections.unmodifiableCollection(allNodes);
    }

    @Override
    public <V> Map<DiVertex<T>, V> createMap() {
        return new HashMap<>();
    }

    @Override
    public Set<DiVertex<T>> createSet() {
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
