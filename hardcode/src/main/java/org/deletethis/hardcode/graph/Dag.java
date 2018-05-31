package org.deletethis.hardcode.graph;

import com.squareup.javapoet.CodeBlock;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Dag<T> {
    private DagVertex<T> root = null;
    private Set<DagVertex<T>> allNodes = new HashSet<>();

    public DagVertex<T> getRoot() {
        return root;
    }

    public void setRoot(DagVertex<T> node) {
        if(node.getInDegree() != 0) {
            throw new IllegalArgumentException();
        }
        root = node;
    }

    public DagVertex<T> createNode(T objectInfo) {
        DagVertex<T> n = new DagVertex<>(this, objectInfo);
        allNodes.add(n);
        return n;
    }

    public void createEdge(DagVertex<T> from, DagVertex<T> to) {
        if(from.getGraph() != this) throw new IllegalArgumentException();
        if(to.getGraph() != this) throw new IllegalArgumentException();

        from.addSuccessor(to);
        to.addPredecessor(from);
    }

    public void printNodes(PrintStream out) {
        for(DagVertex n: allNodes) {
            out.println(n);
        }

    }

    public void printGraphviz(PrintStream out, boolean arrows) {
        printGraphviz(out, arrows, Collections.emptySet());
    }

    public void printGraphviz(PrintStream out, boolean arrows, Set<DagVertex<T>> highlight) {
        String connector = arrows ? " -> " : " -- ";


        if(arrows) {
            out.println("digraph objects {");
        } else {
            out.println("graph objects {");
        }
        for(DagVertex n: allNodes) {
            String c = "";
            if(highlight.contains(n)) {
                c = " color=blue";
            }

            out.println("  " + System.identityHashCode(n) + " [label=" + CodeBlock.of("$S", n.getPayload()) + c + "];");
        }
        for(DagVertex<T> n1: allNodes) {
            for(DagVertex<T> n2: n1.getSuccessors()) {
                out.println("  " + System.identityHashCode(n1) + connector + System.identityHashCode(n2) + ";");
            }
            if(arrows) {
                for(DagVertex<T> n2: n1.getPredecessors()) {
                    out.println("  " + System.identityHashCode(n1) + connector + System.identityHashCode(n2) + " [style=\"dotted\"];");
                }

            }
        }
        out.println("}");
    }




}
