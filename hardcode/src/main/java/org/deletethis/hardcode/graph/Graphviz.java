package org.deletethis.hardcode.graph;


import java.io.*;
import java.util.*;
import java.util.function.Function;

public class Graphviz<T,E> {
    private final Digraph<T,E> digraph;
    private Set<T> highlight = Collections.emptySet();
    private Function<T, Boolean> highlightFunction = (x) -> false;
    private boolean predecessors = false;
    private boolean arrows = true;
    private boolean attributes = true;
    private String highlightStyle = "color=blue";
    private Map<T, ?> marks = null;

    public Graphviz(Digraph<T,E> digraph) {
        this.digraph = digraph;
    }

    public Graphviz<T,E> highlight(Collection<T> highlight) {
        if(highlight == null) {
            this.highlight = Collections.emptySet();
        } else {
            this.highlight = new HashSet<>(highlight);
        }
        return this;
    }

    public Graphviz<T,E> highlight(Function<T, Boolean> hightlightFunction) {
        this.highlightFunction = hightlightFunction;
        return this;
    }

    public Graphviz<T,E> predecessors() {
        this.predecessors = true;
        return this;
    }

    public Graphviz<T,E> noArrows() {
        this.arrows = false;
        return this;
    }

    public Graphviz<T,E> noAttributes() {
        this.attributes = false;
        return this;
    }

    public Graphviz<T,E> highlightStyle(String highlightStyle) {
        this.highlightStyle = highlightStyle;
        return this;
    }


    public Graphviz<T,E> marks(Map<T, ?> marks) {
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

        Map<T, Integer> vertices = digraph.createMap();

        for (T n : digraph.getAllVertices()) {
            int idx = vertices.size();
            vertices.put(n, idx);

            out.print("  " + idx);
            if(attributes) {
                String str = n.toString();
                if(marks != null) {
                    Object o = marks.get(n);
                    if(o != null) {
                        str += "; " + o.toString();
                    }
                }
                out.print(" [label=" + escape(str));
                if (highlight.contains(n) || highlightFunction.apply(n)) {
                    out.print(' ');
                    out.print(highlightStyle);
                }
                out.print("]");
            }
            out.println(";");
        }
        for (T n1 : digraph.getAllVertices()) {
            for (ConnectedVertex<T, E> n2 : digraph.getSuccessorConnections(n1)) {
                out.println("  " + vertices.get(n1) + conn +  vertices.get(n2.getVertex()) + " [label=" + escape(n2.getEdge().toString()) + "];");
            }
            if(predecessors && arrows && attributes) {
                for (T n2 : digraph.getPredecessors(n1)) {
                    out.println("  " + vertices.get(n1) + conn + vertices.get(n2) + " [style=\"dotted\"];");
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

    public void printTemp(String file) {
        String property = System.getProperty("java.io.tmpdir");
        print(new File(property, file));
    }




}
