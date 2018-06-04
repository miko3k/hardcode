package org.deletethis.hardcode.graph;

import java.util.*;

public interface Divertex<T> {
    T getPayload();
    Collection<Divertex<T>> getSuccessors();
    Collection<Divertex<T>> getPredecessors();
    Divertex<T> getSuccessor();
    Divertex<T> getPredecessor();
    int getInDegree();
    int getOutDegree();
    Digraph<T> getGraph();

}
