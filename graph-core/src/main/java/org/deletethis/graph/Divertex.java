package org.deletethis.graph;

import java.util.*;

public interface Divertex<T> {
    T getPayload();
    Collection<Divertex<T>> getSuccessors();
    Collection<Divertex<T>> getPredecessors();
    int getInDegree();
    int getOutDegree();
}
