package org.deletethis.graph;

import java.util.*;

public interface DiVertex<T> {
    T getPayload();
    Collection<DiVertex<T>> getSuccessors();
    Collection<DiVertex<T>> getPredecessors();
    int getInDegree();
    int getOutDegree();
}
