package org.deletethis.hardcode.graph;

import java.util.Collection;
import java.util.Iterator;

public interface Digraph<T, E> extends Graph<T, E> {
    Collection<T> getRoots();
    default T getRoot() {
        Collection<T> roots = getRoots();
        Iterator<T> iterator = roots.iterator();
        if(!iterator.hasNext()) {
            throw new AssertionError("no root");
        }

        T result = iterator.next();
        if(iterator.hasNext()) {
            throw new IllegalStateException("more than one root");
        }
        return result;
    }

    Collection<T> getSuccessors(T vertex);
    Collection<ConnectedVertex<T,E>> getSuccessorConnections(T vertex);
    Collection<T> getPredecessors(T vertex);
    int getInDegree(T vertex);
    int getOutDegree(T vertex);

}
