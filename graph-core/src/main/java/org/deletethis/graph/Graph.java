package org.deletethis.graph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Graph<T,P> {
    T createVertex(P payload);
    void createEdge(T from, T to);
    boolean containsEdge(T from, T to);
    Collection<T> getAllVertices();
    <V> Map<T, V> createMap();
    Set<T> createSet();
    boolean isEmpty();
    boolean isOriented();
}
