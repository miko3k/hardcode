package org.deletethis.hardcode.impl;

import org.deletethis.graph.Digraph;
import org.deletethis.graph.Divertex;
import org.deletethis.graph.algo.DagAlgorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BComponentTree<T> {
    private static class Comp<T> implements BComponent<T> {
        private int size;
        private Divertex<T> root;

        public Comp(Divertex<T> root) {
            this.root = root;
            this.size = 1; // just the root vertex
        }

        @Override
        public Divertex<T> getRoot() {
            return root;
        }

        @Override
        public int getSize() {
            return size;
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
        Set<Divertex<T>> dagVertices = DagAlgorithms.treeVertices(src);

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
                    Divertex<BComponent<T>> targetComponent = map.get(v);
                    if(!out.containsEdge(startComponent, targetComponent)) {
                        out.createEdge(startComponent, targetComponent);
                    }
                    return false;
                }

                Comp<T> comp = (Comp<T>) startComponent.getPayload();
                comp.size++;
                return true;
            });
        }
    }
}
