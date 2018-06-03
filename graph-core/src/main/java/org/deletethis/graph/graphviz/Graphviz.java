package org.deletethis.graph.graphviz;

import org.deletethis.graph.Digraph;
import org.deletethis.graph.Divertex;

import java.io.*;
import java.util.*;

public class Graphviz<T> {
    private final Digraph<T> digraph;
    private Set<Divertex<T>> highlight = Collections.emptySet();
    private boolean predecessors = false;
    private boolean arrows = true;
    private boolean attributes = true;
    private String highlightStyle = "color=blue";
    private Map<Divertex<T>, ?> marks = null;

    public Graphviz(Digraph<T> digraph) {
        this.digraph = digraph;
    }

    public Graphviz<T> highlight(Collection<Divertex<T>> highlight) {
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

    public Graphviz<T> noAttributes() {
        this.attributes = false;
        return this;
    }

    public Graphviz<T> highlightStyle(String highlightStyle) {
        this.highlightStyle = highlightStyle;
        return this;
    }


    public Graphviz<T> marks(Map<Divertex<T>, ?> marks) {
        this.marks = marks;
        return this;
    }

    private String escape(String str) {
        // <http://www.graphviz.org/doc/info/lang.html>
        //    "In quoted strings in DOT, the only escaped character is double-quote (")"

        StringBuilder out = new StringBuilder(str.length()+2);
        out.append("\"");
        for(int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if(c == '"') {
                out.append("\\\"");
            } else {
                out.append(c);
            }
        }
        out.append("\"");
        return out.toString();

    }

    public void print(PrintStream out) {
        String conn = arrows ? " -> " : " -- ";

        if(arrows) {
            out.println("digraph objects {");
        } else {
            out.println("graph objects {");
        }

        for (Divertex n : digraph.getAllVertices()) {
            out.print("  " + System.identityHashCode(n));
            if(attributes) {
                String str = n.getPayload().toString();
                if(marks != null) {
                    Object o = marks.get(n);
                    if(o != null) {
                        str += "; " + o.toString();
                    }
                }
                out.print(" [label=" + escape(str));
                if (highlight.contains(n)) {
                    out.print(' ');
                    out.print(highlightStyle);
                }
                out.print("]");
            }
            out.println(";");
        }
        for (Divertex<T> n1 : digraph.getAllVertices()) {
            for (Divertex<T> n2 : n1.getSuccessors()) {
                out.println("  " + System.identityHashCode(n1) + conn + System.identityHashCode(n2) + ";");
            }
            if(predecessors && arrows && attributes) {
                for (Divertex<T> n2 : n1.getPredecessors()) {
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