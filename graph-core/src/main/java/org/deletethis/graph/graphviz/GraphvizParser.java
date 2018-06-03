package org.deletethis.graph.graphviz;

import org.deletethis.graph.Digraph;
import org.deletethis.graph.Divertex;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GraphvizParser<T> {
    private final VertexFactory<T> vertexSupplier;
    private static final Pattern EDGE_PATTERN = Pattern.compile("^\\s*([0-9a-zA-Z]+)\\s*->\\s*([0-9a-zA-Z]+)\\s*;\\s*$");

    public GraphvizParser(VertexFactory<T> vertexSupplier) {
        this.vertexSupplier = vertexSupplier;
    }

    private Divertex<T> getVertex(Digraph<T> digraph, Map<String, Divertex<T>> vertices, String identifier) {
        Divertex<T> result = vertices.get(identifier);
        if(result == null) {
            T payload = vertexSupplier.getPayload(identifier, null);
            result = digraph.createVertex(payload);
            vertices.put(identifier, result);
        }
        return result;
    }

    public Digraph<T> loadDag(Supplier<? extends Digraph<T>> supplier, Reader reader) throws IOException {
        Digraph<T> digraph = supplier.get();
        Map<String, Divertex<T>> vertices = new HashMap<>();

        BufferedReader buf = new BufferedReader(reader);
        String line;
        while((line = buf.readLine()) != null) {
            Matcher matcher = EDGE_PATTERN.matcher(line);
            if(!matcher.matches()) {
                continue;
            }
            Divertex<T> v1 = getVertex(digraph, vertices, matcher.group(1));
            Divertex<T> v2 = getVertex(digraph, vertices, matcher.group(2));

            digraph.createEdge(v1, v2);
        }
        return digraph;
    }

    public Digraph<T> loadDag(Supplier<? extends Digraph<T>> supplier, InputStream inputStream) throws IOException {
        return loadDag(supplier, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public Digraph<T> loadDag(Supplier<? extends Digraph<T>> supplier, File file) throws IOException {
        try(InputStream stream = new FileInputStream(file)) {
            return loadDag(supplier, stream);
        }
    }

    public Digraph<T> loadDag(Supplier<? extends Digraph<T>> supplier, String fileName) throws IOException {
        return loadDag(supplier, new File(fileName));
    }
}
