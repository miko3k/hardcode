package org.deletethis.graph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Dag<T> extends Graph<DiVertex<T>, T>, Rooted<DiVertex<T>> {
    DiVertex<T> getRoot();
    void setRoot(DiVertex<T> node);
    DiVertex<T> createVertex(T objectInfo);
    void createEdge(DiVertex<T> from, DiVertex<T> to);
    Collection<DiVertex<T>> getAllVertices();
    <V> Map<DiVertex<T>, V> createMap();
    Set<DiVertex<T>> createSet();
    boolean isEmpty();
}
