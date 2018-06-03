package org.deletethis.graph.algo;

import org.deletethis.graph.Digraph;
import org.deletethis.graph.Divertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BComponentTree<T> {
    private static class Comp<T> implements BComponent<T> {
        private int size;
        private Divertex<T> root;

        public Comp(Divertex<T> root) {
            this.root = root;
            this.size = 0;
        }

        @Override
        public Divertex<T> getRoot() {
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

    private final Digraph<BComponent<T>> out;
    private final Digraph<T> src;


    public BComponentTree(Digraph<BComponent<T>> out, Digraph<T> src) {
        this.out = out;
        this.src = src;
    }

    public void run() {
        Divertex<T> root = src.getRoots().iterator().next();
        Set<Divertex<T>> dagVertices = DagAlgorithms.findArticulationPoints(root, false);

        Map<Divertex<T>, Divertex<BComponent<T>>> map = new HashMap<>();
        for(Divertex<T> v: dagVertices) {
            map.put(v, out.createVertex(new Comp<>(v)));
        }

        for(Divertex<T> start: dagVertices) {
            Divertex<BComponent<T>> startComponent = map.get(start);

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
