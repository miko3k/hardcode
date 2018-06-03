package org.deletethis.graph.algo;

import org.deletethis.graph.Divertex;

public interface BComponent<T> {
    Divertex<T> getRoot();
    int getSize();
}
