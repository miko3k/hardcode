package org.deletethis.hardcode.graph;

import com.squareup.javapoet.CodeBlock;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

public class Graph {
    private Node root;
    private Set<Node> allNodes;

    public Graph(Node n, Set<Node> allNodes) {
        this.root = n;
        this.allNodes = allNodes;
    }

    public Node getRoot() {
        return root;
    }

    public void printNodes(PrintStream out) {
        for(Node n: allNodes) {
            out.println(n);
        }

    }

    public void printGraphviz(PrintStream out, boolean arrows) {
        String connector = arrows ? " -> " : " -- ";

        if(arrows) {
            out.println("digraph objects {");
        } else {
            out.println("graph objects {");
        }
        for(Node n: allNodes) {
            out.println("  " + System.identityHashCode(n) + " [label=" + CodeBlock.of("$S", n.getConstructor().toString()) + "];");
        }
        for(Node n1: allNodes) {
            for(Node n2: n1.getParameters()) {
                out.println("  " + System.identityHashCode(n1) + connector + System.identityHashCode(n2) + ";");
            }
        }
        out.println("}");
    }

    public class ArticulationPoints {
        HashMap<Node, Boolean> visited = new HashMap<>();
        HashMap<Node, Integer> depth = new HashMap<>();
        HashMap<Node, Integer> low = new HashMap<>();

        public void getArticulationPoints(Node i, int d) {
            visited.put(i, true);
            depth.put(i, d);
            low.put(i, d);
            int childCount = 0;
            boolean isArticulation = false;


        }
    }

/*
GetArticulationPoints(i, d)
    visited[i] = true
    depth[i] = d
    low[i] = d
    childCount = 0
    isArticulation = false
    for each ni in adj[i]
        if not visited[ni]
            parent[ni] = i
            GetArticulationPoints(ni, d + 1)
            childCount = childCount + 1
            if low[ni] >= depth[i]
                isArticulation = true
            low[i] = Min(low[i], low[ni])
        else if ni <> parent[i]
            low[i] = Min(low[i], depth[ni])
    if (parent[i] <> null and isArticulation) or (parent[i] == null and childCount > 1)
        Output i as articulation point
*/
}
