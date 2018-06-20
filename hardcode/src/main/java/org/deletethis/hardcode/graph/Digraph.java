package org.deletethis.hardcode.graph;

import java.util.Collection;
import java.util.Iterator;

public interface Digraph<T> extends Graph<Divertex<T>, T> {
    Collection<Divertex<T>> getRoots();
    default Divertex<T> getRoot() {
        Collection<Divertex<T>> roots = getRoots();
        Iterator<Divertex<T>> iterator = roots.iterator();
        if(!iterator.hasNext()) {
            throw new AssertionError("no root");
        }

        Divertex<T> result = iterator.next();
        if(iterator.hasNext()) {
            throw new IllegalStateException("more than one root");
        }
        return result;
    }

    Collection<Divertex<T>> getSuccessors(Divertex<T> vertex);
    Collection<Divertex<T>> getPredecessors(Divertex<T> vertex);
    int getInDegree(Divertex<T> vertex);
    int getOutDegree(Divertex<T> vertex);

}
