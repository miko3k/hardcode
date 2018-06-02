package org.deletethis.hardcode.graph;

import com.squareup.javapoet.CodeBlock;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Graphviz<T> {
    private final Dag<T> dag;
    private Set<Vertex<T>> highlight = Collections.emptySet();
    private boolean predecessors = false;
    private boolean arrows = true;

    public Graphviz(Dag<T> dag) {
        this.dag = dag;
    }

    public Graphviz<T> highlight(Collection<Vertex<T>> highlight) {
        if(highlight == null) {
            this.highlight = Collections.emptySet();
        } else {
            this.highlight = new HashSet<>(highlight);
        }
        return this;
    }

    public Graphviz<T> predecessors() {
        this.predecessors = true;
        return this;
    }

    public Graphviz<T> noArrows() {
        this.arrows = false;
        return this;
    }


    public void print(PrintStream out) {
        String conn = arrows ? " -> " : " -- ";

        if(arrows) {
            out.println("digraph objects {");
        } else {
            out.println("graph objects {");
        }

        for (Vertex n : dag.getAllVertices()) {
            String c = "";
            if (highlight.contains(n)) {
                c = " color=blue";
            }

            out.println("  " + System.identityHashCode(n) + " [label=" + CodeBlock.of("$S", n.getPayload()) + c + "];");
        }
        for (Vertex<T> n1 : dag.getAllVertices()) {
            for (Vertex<T> n2 : n1.getSuccessors()) {
                out.println("  " + System.identityHashCode(n1) + conn + System.identityHashCode(n2) + ";");
            }
            if(predecessors && arrows) {
                for (Vertex<T> n2 : n1.getPredecessors()) {
                    out.println("  " + System.identityHashCode(n1) + conn + System.identityHashCode(n2) + " [style=\"dotted\"];");
                }
            }
        }
        out.println("}");
    }

    public void print(File file) {
        try(PrintStream printStream = new PrintStream(new FileOutputStream(file))) {
            print(printStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void print(String file) {
        print(new File(file));
    }




}
