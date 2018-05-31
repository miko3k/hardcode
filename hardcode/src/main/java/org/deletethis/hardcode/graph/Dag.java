package org.deletethis.hardcode.graph;

import com.squareup.javapoet.CodeBlock;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Dag {
    private Node root = null;
    private Set<Node> allNodes = new HashSet<>();

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node node) {
        if(node.getInDegree() != 0) {
            throw new IllegalArgumentException();
        }
        root = node;
    }

    public Node createNode(ObjectInfo objectInfo) {
        Node n = new Node(this, objectInfo);
        allNodes.add(n);
        return n;
    }

    public void createEdge(Node from, Node to) {
        if(from.getGraph() != this) throw new IllegalArgumentException();
        if(to.getGraph() != this) throw new IllegalArgumentException();

        from.addSuccessor(to);
        to.addPredecessor(from);
    }

    public void printNodes(PrintStream out) {
        for(Node n: allNodes) {
            out.println(n);
        }

    }

    public void printGraphviz(PrintStream out, boolean arrows) {
        printGraphviz(out, arrows, Collections.emptySet());
    }

    public void printGraphviz(PrintStream out, boolean arrows, Set<Node> highlight) {
        String connector = arrows ? " -> " : " -- ";


        if(arrows) {
            out.println("digraph objects {");
        } else {
            out.println("graph objects {");
        }
        for(Node n: allNodes) {
            String c = "";
            if(highlight.contains(n)) {
                c = " color=blue";
            }

            out.println("  " + System.identityHashCode(n) + " [label=" + CodeBlock.of("$S", n.getObjectInfo()) + c + "];");
        }
        for(Node n1: allNodes) {
            for(Node n2: n1.getSuccessors()) {
                out.println("  " + System.identityHashCode(n1) + connector + System.identityHashCode(n2) + ";");
            }
            if(arrows) {
                for(Node n2: n1.getPredecessors()) {
                    out.println("  " + System.identityHashCode(n1) + connector + System.identityHashCode(n2) + " [style=\"dotted\"];");
                }

            }
        }
        out.println("}");
    }




}
