package org.deletethis.graph;

import org.deletethis.graph.algo.DagAlgorithms;
import org.deletethis.graph.graphviz.Graphviz;
import org.deletethis.graph.graphviz.GraphvizParser;

import java.io.IOException;
import java.util.Set;

public class Load {
    public static void main(String[] args) throws IOException  {
        GraphvizParser<String> str = new GraphvizParser<>((name, attrs) -> name);

        Digraph<String> stringDag = str.loadDag(MapDigraph::new, Load.class.getResourceAsStream("/gf.gv"));
        Set<Divertex<String>> divertices = DagAlgorithms.treeVertices(stringDag);
    }
}
