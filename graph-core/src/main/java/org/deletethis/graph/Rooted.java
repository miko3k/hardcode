package org.deletethis.graph;

public interface Rooted<T> {
    T getRoot();
    void setRoot(T node);
}
