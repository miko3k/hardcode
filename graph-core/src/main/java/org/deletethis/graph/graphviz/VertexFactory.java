package org.deletethis.graph.graphviz;

import java.util.Map;

public interface VertexFactory<T> {
    T getPayload(String name, Map<String, String> attrs);
}
