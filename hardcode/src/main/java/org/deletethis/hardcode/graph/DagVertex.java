package org.deletethis.hardcode.graph;

import java.util.*;

public interface DagVertex<T> {
    T getPayload();
    Collection<DagVertex<T>> getSuccessors();
    Collection<DagVertex<T>> getPredecessors();
    int getInDegree();
    int getOutDegree();
}
