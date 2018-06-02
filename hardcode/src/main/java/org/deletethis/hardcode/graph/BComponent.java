package org.deletethis.hardcode.graph;

public interface BComponent<T> {
    Vertex<T> getRoot();
    int getSize();
}
