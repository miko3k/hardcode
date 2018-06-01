package org.deletethis.hardcode.graph;

import java.util.*;

public class DagImpl<T> implements Dag<T> {
    private Vertex root = null;
    private Set<DagVertex<T>> allNodes = new HashSet<>();

    private class Vertex implements DagVertex<T> {
        private final T payload;
        private final List<Vertex> successors = new ArrayList<>();
        /** same node may appear here multipe times, if it appears several times as a successor of the other node */
        private final List<Vertex> predecessors = new ArrayList<>();

        private Vertex(T payload) {
            this.payload = payload;
        }

        public T getPayload() {
            return payload;
        }

        public Collection<DagVertex<T>> getSuccessors() {
            return Collections.unmodifiableList(successors);
        }

        void addPredecessor(Vertex node) {
            predecessors.add(node);
        }

        void addSuccessor(Vertex node) {
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
            for(Vertex n: successors) {
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

        public Collection<DagVertex<T>> getPredecessors() {
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

    public DagVertex<T> getRoot() {
        return root;
    }

    private Vertex mine(DagVertex<T> node) {
        Vertex v = (Vertex) node;
        if(v.getGraph() != this)
            throw new IllegalArgumentException();

        return v;
    }

    public void setRoot(DagVertex<T> node) {
        if(node.getInDegree() != 0) {
            throw new IllegalArgumentException();
        }
        root = mine(node);
    }

    public DagVertex<T> createVertex(T objectInfo) {
        DagVertex<T> n = new Vertex(objectInfo);
        allNodes.add(n);
        return n;
    }

    public void createEdge(DagVertex<T> from, DagVertex<T> to) {
        Vertex f = mine(from);
        Vertex t = mine(to);

        f.addSuccessor(t);
        t.addPredecessor(f);
    }

    @Override
    public Collection<DagVertex<T>> getAllVertices() {
        return Collections.unmodifiableCollection(allNodes);
    }
}
