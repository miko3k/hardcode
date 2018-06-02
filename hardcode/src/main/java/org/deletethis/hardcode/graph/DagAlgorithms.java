package org.deletethis.hardcode.graph;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DagAlgorithms {
    private DagAlgorithms() {
    }

    public static void printNodes(Dag<?> dag, PrintStream out) {
        for(Vertex n: dag.getAllVertices()) {
            out.println(n);
        }
    }


    public static <T> Set<Vertex<T>> findArticulationPoints(Vertex<T> n, boolean includeRoot) {
        ArticulationPoints<T> articulationPoints = new ArticulationPoints<>(includeRoot);
        articulationPoints.find(n, 0);
        return articulationPoints.out;
    }

    private static <T> void dfs(Set<Vertex<T>> visited, Vertex<T> vertex, Function<Vertex<T>, Boolean> fn) {
        if(!fn.apply(vertex))
            return;

        visited.add(vertex);
        for(Vertex<T> v: vertex.getSuccessors()) {
            if(visited.contains(v))
                continue;

            dfs(visited, v, fn);
        }
    }

    public static <T> void dfs(Vertex<T> vertex, Function<Vertex<T>, Boolean> fn) {
        Set<Vertex<T>> visited = new HashSet<>();
        dfs(visited, vertex, fn);
    }

    public static <T> Dag<BComponent<T>> getBComponents(Dag<BComponent<T>> out, Dag<T> src) {
        BComponentTree<T> alg = new BComponentTree<>(out, src);
        alg.run();
        return out;
    }

}
