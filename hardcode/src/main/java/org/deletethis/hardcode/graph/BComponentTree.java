package org.deletethis.hardcode.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BComponentTree<T> {
    private static class Comp<T> implements BComponent<T> {
        private int size;
        private Vertex<T> root;

        public Comp(Vertex<T> root) {
            this.root = root;
            this.size = 0;
        }

        @Override
        public Vertex<T> getRoot() {
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
        Set<Vertex<T>> dagVertices = DagAlgorithms.findArticulationPoints(src.getRoot(), false);

        Map<Vertex<T>, Vertex<BComponent<T>>> map = new HashMap<>();
        for(Vertex<T> v: dagVertices) {
            map.put(v, out.createVertex(new Comp<>(v)));
        }

        for(Vertex<T> start: dagVertices) {
            Vertex<BComponent<T>> startComponent = map.get(start);

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
