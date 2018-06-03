package org.deletethis.graph.algo;

import org.deletethis.graph.DiVertex;

public interface BComponent<T> {
    DiVertex<T> getRoot();
    int getSize();
}
