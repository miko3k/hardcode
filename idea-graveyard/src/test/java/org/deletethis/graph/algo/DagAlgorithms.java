package org.deletethis.graph.algo;

import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Divertex;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;


@SuppressWarnings({"unused", "WeakerAccess"})
public class DagAlgorithms {
    private DagAlgorithms() {
    }

    public static void printNodes(Digraph<?> digraph, PrintStream out) {
        for(Divertex n: digraph.getAllVertices()) {
            out.println(n);
        }
    }


    public static <T> Set<Divertex<T>> findArticulationPoints(Divertex<T> n, boolean includeRoot) {
        ArticulationPoints<T> articulationPoints = new ArticulationPoints<>(includeRoot);
        articulationPoints.find(n, 0);
        return articulationPoints.out;
    }

    private static <T> void dfs(Set<Divertex<T>> visited, Divertex<T> vertex, Function<Divertex<T>, Boolean> fn) {
        if(!fn.apply(vertex))
            return;

        visited.add(vertex);
        for(Divertex<T> v: vertex.getSuccessors()) {
            if(visited.contains(v))
                continue;

            dfs(visited, v, fn);
        }
    }

    public static <T> void dfs(Divertex<T> vertex, Function<Divertex<T>, Boolean> fn) {
        Set<Divertex<T>> visited = new HashSet<>();
        dfs(visited, vertex, fn);
    }

    public static <T> Iterable<Divertex<T>> topologicalSort(Digraph<T> graph) {
        return () -> new TopoSort<>(graph, graph.getRoots());
    }

    public static <T> Iterable<Divertex<T>> topologicalSort(Divertex<T> vertex) {
        return () -> new TopoSort<>(vertex.getGraph(), Collections.singletonList(vertex));
    }

    public static <T> Set<Divertex<T>> treeVertices(Digraph<T> graph) {
        return new TreeVertices<>(graph).run();
    }

    public static <T> Set<Divertex<T>> treeVertices2(Digraph<T> graph) {
        return new TreeVertices2<>(graph).run(graph.getRoot());
    }
}
