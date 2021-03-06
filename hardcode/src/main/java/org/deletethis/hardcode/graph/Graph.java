package org.deletethis.hardcode.graph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Graph<T,E> {
    void createEdge(T from, T to, E edge);
    boolean containsEdge(T from, T to);
    Collection<T> getAllVertices();
    <V> Map<T, V> createMap();
    Set<T> createSet();
    boolean isEmpty();
    boolean isOriented();
    void addVertex(T vertex);
}
