package org.deletethis.hardcode.graph;

import java.util.*;

public class DagVertex<T> {
    private final Dag<T> graph;
    private final T payload;
    private final List<DagVertex<T>> successors = new ArrayList<>();
    /** same node may appear here multipe times, if it appears several times as a successor of the other node */
    private final List<DagVertex<T>> predecessors = new ArrayList<>();

    public DagVertex(Dag<T> graph, T payload) {
        this.graph = graph;
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public List<DagVertex<T>> getSuccessors() {
        return Collections.unmodifiableList(successors);
    }


    void addPredecessor(DagVertex<T> node) {
        predecessors.add(node);
    }

    void addSuccessor(DagVertex<T> node) {
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
        for(DagVertex n: successors) {
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

    public List<DagVertex<T>> getPredecessors() {
        return Collections.unmodifiableList(predecessors);
    }

    public int getInDegree() {
        return predecessors.size();
    }

    public int getOutDegree() {
        return successors.size();
    }

    public Dag<T> getGraph() {
        return graph;
    }
}
