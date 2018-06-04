package org.deletethis.graph.algo;

import java.util.Map;

public interface VertexFactory<T> {
    T getPayload(String name, Map<String, String> attrs);
}
