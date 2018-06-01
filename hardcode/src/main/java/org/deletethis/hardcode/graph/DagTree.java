package org.deletethis.hardcode.graph;

import java.util.Set;

public class DagTree<T> {
    private final Dag<Integer> out;
    private final Dag<T> src;


    public DagTree(Dag<Integer> out, Dag<T> src) {
        this.out = out;
        this.src = src;
    }

    public void run() {
        Set<DagVertex<T>> dagVertices = ArticulationPoints.find(src.getRoot(), false);
        for(DagVertex<T> v: dagVertices) {

        }
    }
}
