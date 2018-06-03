package org.deletethis.graph.algo;

import org.deletethis.graph.Dag;
import org.deletethis.graph.DiVertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BComponentTree<T> {
    private static class Comp<T> implements BComponent<T> {
        private int size;
        private DiVertex<T> root;

        public Comp(DiVertex<T> root) {
            this.root = root;
            this.size = 0;
        }

        @Override
        public DiVertex<T> getRoot() {
            return null;
        }

        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public String toString() {
            return root.getPayload().toString() + " [" + size + "]";
        }
    }

    private final Dag<BComponent<T>> out;
    private final Dag<T> src;


    public BComponentTree(Dag<BComponent<T>> out, Dag<T> src) {
        this.out = out;
        this.src = src;
    }

    public void run() {
        Set<DiVertex<T>> dagVertices = DagAlgorithms.findArticulationPoints(src.getRoot(), false);

        Map<DiVertex<T>, DiVertex<BComponent<T>>> map = new HashMap<>();
        for(DiVertex<T> v: dagVertices) {
            map.put(v, out.createVertex(new Comp<>(v)));
        }

        for(DiVertex<T> start: dagVertices) {
            DiVertex<BComponent<T>> startComponent = map.get(start);

            DagAlgorithms.dfs(start, v -> {
                if(v == start) {
                    return true;
                }
                if(dagVertices.contains(v)) {
                    out.createEdge(startComponent, map.get(v));
                    return false;
                }

                Comp<T> comp = (Comp<T>) startComponent.getPayload();
                comp.size++;
                return true;
            });
        }
    }
}
