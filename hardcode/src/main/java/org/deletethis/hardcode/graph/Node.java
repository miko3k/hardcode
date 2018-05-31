package org.deletethis.hardcode.graph;

import java.util.*;

public class Node {
    private final ObjectInfo objectInfo;
    private final List<Node> successors;
    /** same not may appear here multipe times, if it appears several times as a paramter of other node */
    private final List<Node> predecessors = new ArrayList<>();

    Node(ObjectInfo objectInfo, List<Node> successors) {
        this.objectInfo = objectInfo;
        this.successors = successors;
    }

    public ObjectInfo getObjectInfo() {
        return objectInfo;
    }

    public List<Node> getSuccessors() {
        return successors;
    }


    public void addPredecessor(Node node) {
        predecessors.add(node);
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

    public int getRefCount() {
        return predecessors.size();
    }

    public List<Node> getPredecessors() {
        return predecessors;
    }
}
