package org.deletethis.hardcode.graph;

import java.util.*;

public class Node {
    private final Dag graph;
    private final ObjectInfo objectInfo;
    private final List<Node> successors = new ArrayList<>();
    /** same node may appear here multipe times, if it appears several times as a successor of the other node */
    private final List<Node> predecessors = new ArrayList<>();

    public Node(Dag graph, ObjectInfo objectInfo) {
        this.graph = graph;
        this.objectInfo = objectInfo;
    }

    public ObjectInfo getObjectInfo() {
        return objectInfo;
    }

    public List<Node> getSuccessors() {
        return Collections.unmodifiableList(successors);
    }


    void addPredecessor(Node node) {
        predecessors.add(node);
    }

    void addSuccessor(Node node) {
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
        bld.append(objectInfo);

        boolean first = true;
        for(Node n: successors) {
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

    public List<Node> getPredecessors() {
        return Collections.unmodifiableList(predecessors);
    }

    public int getInDegree() {
        return predecessors.size();
    }

    public int getOutDegree() {
        return successors.size();
    }

    public Dag getGraph() {
        return graph;
    }
}
