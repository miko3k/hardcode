package org.deletethis.hardcode.graph;

import java.util.*;

public interface Vertex<T> {
    T getPayload();
    Collection<Vertex<T>> getSuccessors();
    Collection<Vertex<T>> getPredecessors();
    int getInDegree();
    int getOutDegree();
}
