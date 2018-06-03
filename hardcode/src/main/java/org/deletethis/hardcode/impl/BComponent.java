package org.deletethis.hardcode.impl;

import org.deletethis.graph.Divertex;

public interface BComponent<T> {
    Divertex<T> getRoot();
    int getSize();
}
