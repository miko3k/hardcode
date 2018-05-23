package org.deletethis.hardcode.graph;

import java.io.PrintStream;
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
}
