package org.deletethis.graph.algo;

import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Divertex;

/**
 * A semi-brute force algorithm to find "tree vertices"
 *
 *   <https://stackoverflow.com/questions/50657404/finding-all-tree-like-vertices-in-a-dag>
 *
 * Still in search of a better algorithm.
 *
 * Brute force approach would involve removing a vertex, and then checking if any successor
 * is still reachable (ignoring orientation). This algorithm is a slight improvement by splitting
 * graph into tree separate trees and processing them one by one. Should provide speed up if input
 * graph is almost a tree, which it is
 *
 * This one works as following:
 *
 *    find(root) -> [vertices]
 *        # optimization for straight lines
 *
 *        out = []
 *        blacklist = []
 *
 *        while successor count of root is 1:
 *           out += [ root ]
 *           root = root.successor
 *
 *        for v in all vertices under root in topological order
 *            if v is on blacklist
 *                continue
 *
 *            remove remove vertices out + [v] (for this iteration only)
 *            run DFS, ignoring orientation
 *            if any successor of v was reached
 *                out += [ v ]
 *                add entire subtree under v, including v to blacklist
 *
 *            if v was not reached
 *                break
 *
 *        result = out
 *        for v in out:
 *            result += find(v)
 *
 *        return result
 *
 * DFS prefers predecessors for performance (NEED VERIFICATION IF IT'S ACTUALLY FASTER)
 * topological order is necessary, so we don't discover successor after predecessor
 *
 */
import java.util.Set;

class TreeVertices2<T> {
    private final Digraph<T> graph;

    TreeVertices2(Digraph<T> graph) {
        this.graph = graph;
    }

    private enum Reachability {
        FOUND_MAIN_VERTEX,
        FOUND_NOTHING,
        FOUND_SUCCESSOR;

        Reachability combine(Reachability curr) {
            switch (this) {
                case FOUND_NOTHING: return curr;
                case FOUND_MAIN_VERTEX:
                    switch (curr) {
                        case FOUND_MAIN_VERTEX: return Reachability.FOUND_MAIN_VERTEX;
                        case FOUND_NOTHING: return Reachability.FOUND_MAIN_VERTEX;
                        case FOUND_SUCCESSOR: return Reachability.FOUND_SUCCESSOR;
                    }
                    break;
                case FOUND_SUCCESSOR:
                    return Reachability.FOUND_SUCCESSOR;
            }
            throw new IllegalStateException();
        }
    }

    private void addSubtree(Set<Divertex<T>> list, Divertex<T> root) {
        if(list.contains(root))
            return;

        list.add(root);

        for(Divertex<T> v: root.getSuccessors()) {
            addSubtree(list, v);
        }
    }


    private Reachability getReachibility(Set<Divertex<T>> visited, Divertex<T> mainVertex, Divertex<T> root, Divertex<T> current) {
        if(current == mainVertex) {
            return Reachability.FOUND_MAIN_VERTEX;
        }
        if(visited.contains(current)) {
            return Reachability.FOUND_NOTHING;
        }
        if(mainVertex.getSuccessors().contains(current)) {
            return Reachability.FOUND_SUCCESSOR;
        }
        Reachability r = Reachability.FOUND_NOTHING;
        visited.add(current);
        if(current != root) {
            for (Divertex<T> v : current.getPredecessors()) {
                r = r.combine(getReachibility(visited, mainVertex, root, v));
                if (r == Reachability.FOUND_SUCCESSOR)
                    return Reachability.FOUND_SUCCESSOR;
            }
        }
        for(Divertex<T> v: current.getSuccessors()) {
            r = r.combine(getReachibility(visited, mainVertex, root, v));
            if(r == Reachability.FOUND_SUCCESSOR)
                return Reachability.FOUND_SUCCESSOR;
        }
        return r;
    }

    Set<Divertex<T>> run(Divertex<T> root) {
        System.out.println("RUN, root = " + root);
        Set<Divertex<T>> out = graph.createSet();
        Set<Divertex<T>> blacklist = graph.createSet();
        // optimization for straight lines
        System.out.println(root.getOutDegree());
        while(root.getOutDegree() == 1) {
            out.add(root);
            System.out.println("++ " + root);
            root = root.getSuccessor();
        }
        // leaf node, just let it be
        if(root.getOutDegree() == 0) {
            return out;
        }

        for(Divertex<T> v: DagAlgorithms.topologicalSort(root)) {
            // ignore root
            if(v == root || blacklist.contains(v) || v.getOutDegree() == 0)
                continue;

            Set<Divertex<T>> ignored = graph.createSet();
            ignored.addAll(out);

            Reachability reachability  = getReachibility(ignored, v, root, root);
            if(reachability == Reachability.FOUND_NOTHING) {
                break;
            } else if(reachability == Reachability.FOUND_MAIN_VERTEX) {
                out.add(v);
                System.out.println("+ " + v);
                addSubtree(blacklist, v);
            } else if(reachability == Reachability.FOUND_SUCCESSOR) {
                // do nothing
            } else {
                throw new IllegalStateException();
            }
        }
        Set<Divertex<T>> result = graph.createSet();
        result.addAll(out);
        for(Divertex<T> v: out) {
            result.addAll(run(v));
        }

        return result;
    }
}
