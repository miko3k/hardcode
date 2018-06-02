package org.deletethis.hardcode.graph;

import java.util.Collection;

public interface Dag<T> {
    Vertex<T> getRoot();
    void setRoot(Vertex<T> node);
    Vertex<T> createVertex(T objectInfo);
    void createEdge(Vertex<T> from, Vertex<T> to);
    Collection<Vertex<T>> getAllVertices();
    boolean isEmpty();
}
