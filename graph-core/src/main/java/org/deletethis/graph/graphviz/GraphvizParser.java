package org.deletethis.graph.graphviz;

import org.deletethis.graph.Dag;

import java.io.Reader;
import java.util.function.Supplier;

public class GraphvizParser<T> {
    private final Supplier<T> vertexSupplier;

    public GraphvizParser(Supplier<T> vertexSupplier) {
        this.vertexSupplier = vertexSupplier;
    }

    Dag<T> loadDag(Supplier<? extends Dag<T>> supplier, Reader reader) {
        return null;
    }
}
