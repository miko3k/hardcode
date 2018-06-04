package org.deletehis.hardcode.impl;

import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.graph.Graphviz;

import java.util.*;
import java.util.function.Supplier;

public class Splitter<T> {
    private Map<Divertex<BComponent<T>>, Integer> totals;
    private Digraph<BComponent<T>> tree;
    private Digraph<T> src;
    private static final int SIZE = 7;


    public Splitter(Digraph<BComponent<T>> tree, Digraph<T> src) {
        this.tree = tree;
        this.src = src;
    }

    private int computeTotals(Divertex<BComponent<T>> vertex) {
        int total = vertex.getPayload().getSize();
        for(Divertex<BComponent<T>> successor: vertex.getSuccessors()) {
            total += computeTotals(successor);
        }
        if(totals.containsKey(vertex)) {
            throw new IllegalStateException("not a tree!");
        }
        System.out.println("t = " + total);
        totals.put(vertex, total);
        return total;
    }

    private void eliminateTotals(Divertex<BComponent<T>> key) {
        // might be already eliminated
        if(!totals.containsKey(key))
            return;

        for(Divertex<BComponent<T>> successor: key.getSuccessors()) {
            eliminateTotals(successor);
        }

        totals.remove(key);
    }

    private void eliminate(Divertex<BComponent<T>> key) {
        int currentTotal = totals.get(key);
        eliminateTotals(key);

        while(true) {
            Collection<Divertex<BComponent<T>>> predecessors = key.getPredecessors();
            Iterator<Divertex<BComponent<T>>> iterator = predecessors.iterator();
            if(!iterator.hasNext()) {
                return;
            }
            // first move, as current entry was already removed
            key = iterator.next();
            int value = totals.get(key);
            value -= currentTotal;
            totals.put(key, value);


            if(iterator.hasNext()) {
                throw new IllegalStateException("not a tree");
            }
        }
    }


    public void run() {
        List<Divertex<T>> out = new ArrayList<>();
        BComponentTree<T> alg = new BComponentTree<>(tree, src);
        alg.run();

        totals = tree.createMap();

        computeTotals(tree.getRoot());

        int iteration = 0;
        while(true) {
            new Graphviz<>(tree).marks(totals).print("target/aaa"+iteration+".gv");
            ++iteration;
            Map.Entry<Divertex<BComponent<T>>, Integer> smallest = null;
            Map.Entry<Divertex<BComponent<T>>, Integer> largest = null;
            for (Map.Entry<Divertex<BComponent<T>>, Integer> e : totals.entrySet()) {
                if (e.getValue() > SIZE) {
                    if (smallest == null || smallest.getValue() > e.getValue()) {
                        smallest = e;
                    }
                } else {
                    if (largest == null || largest.getValue() < e.getValue()) {
                        largest = e;
                    }
                }
            }
            Map.Entry<Divertex<BComponent<T>>, Integer> best = Objects.requireNonNull(largest != null ? largest : smallest);

            Divertex<BComponent<T>> cmp = best.getKey();
            System.out.println(">> [" + (iteration-1) + "] " + best);
            out.add(cmp.getPayload().getRoot());
            if(cmp == tree.getRoot())
                break;

            eliminate(cmp);
        }
        new Graphviz<>(tree).marks(totals).print("target/aaa"+iteration+".gv");
    }

    public static <T> void getBComponents(Supplier<Digraph<BComponent<T>>> tree, Digraph<T> src) {
        new Splitter<>(tree.get(), src).run();



    }
}
