package org.deletethis.hardcode.graph;

import java.util.Collection;

public interface Dag<T> {
    DagVertex<T> getRoot();
    void setRoot(DagVertex<T> node);
    DagVertex<T> createVertex(T objectInfo);
    void createEdge(DagVertex<T> from, DagVertex<T> to);
    Collection<DagVertex<T>> getAllVertices();
}
