package org.deletehis.hardcode.impl;

import org.deletethis.hardcode.graph.Divertex;

public interface BComponent<T> {
    Divertex<T> getRoot();
    int getSize();
}
