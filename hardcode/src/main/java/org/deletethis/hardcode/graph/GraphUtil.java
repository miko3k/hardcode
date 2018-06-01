package org.deletethis.hardcode.graph;

import com.squareup.javapoet.CodeBlock;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Function;

public class GraphUtil {
    private GraphUtil() { }

    public static  void printNodes(Dag<?> dag, PrintStream out) {
        for(DagVertex n: dag.getAllVertices()) {
            out.println(n);
        }

    }

    public static void printGraphviz(Dag<?> dag, PrintStream out) {
        printGraphviz(dag, out, Collections.emptySet());
    }

    public static <T> void printGraphviz(Dag<T> dag, PrintStream out, Set<DagVertex<T>> highlight) {
        out.println("digraph objects {");

        for (DagVertex n : dag.getAllVertices()) {
            String c = "";
            if (highlight.contains(n)) {
                c = " color=blue";
            }

            out.println("  " + System.identityHashCode(n) + " [label=" + CodeBlock.of("$S", n.getPayload()) + c + "];");
        }
        for (DagVertex<T> n1 : dag.getAllVertices()) {
            for (DagVertex<T> n2 : n1.getSuccessors()) {
                out.println("  " + System.identityHashCode(n1) + " -> " + System.identityHashCode(n2) + ";");
            }
            for (DagVertex<T> n2 : n1.getPredecessors()) {
                out.println("  " + System.identityHashCode(n1) + " -> " + System.identityHashCode(n2) + " [style=\"dotted\"];");
            }
        }
        out.println("}");
    }

    public static <T> void dfs(Set<DagVertex<T>> visited, DagVertex<T> vertex, Function<DagVertex<T>, Boolean> fn) {
        if(!fn.apply(vertex))
            return;

        visited.add(vertex);
        for(DagVertex<T> v: vertex.getSuccessors()) {
            if(visited.contains(v))
                continue;

            dfs(visited, v, fn);
        }
    }

    public static boolean isTree(Dag<?> dag) {
        for(DagVertex<?> v: dag.getAllVertices()) {
            if(v == dag.getRoot()) {
                if(v.getInDegree() != 0)
                    return false;
            } else {
                if(v.getInDegree() != 1)
                    return false;
            }
        }
        return false;
    }

    public static <T> void dfs(DagVertex<T> vertex, Function<DagVertex<T>, Boolean> fn) {
        Set<DagVertex<T>> visited = new HashSet<>();
        dfs(visited, vertex, fn);
    }
}
