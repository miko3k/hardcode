package org.deletethis.hardcode.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ArticulationPoints<T> {
    private final Set<DagVertex<T>> visited = new HashSet<>();
    private final HashMap<DagVertex<T>, Integer> depth = new HashMap<>();
    private final HashMap<DagVertex<T>, Integer> low = new HashMap<>();
    private final HashMap<DagVertex<T>, DagVertex<T>> parent = new HashMap<>();
    private final Set<DagVertex<T>> out = new HashSet<>();
    private final boolean includeRoot;

    public ArticulationPoints(boolean includeRoot) {
        this.includeRoot = includeRoot;
    }

    private void find(DagVertex<T> i, int d) {
        /*
        GetArticulationPoints(i, d)
            visited[i] = true
            depth[i] = d
            low[i] = d
            childCount = 0
            isArticulation = false
            for each ni in adj[i]
                if not visited[ni]
                    parent[ni] = i
                    GetArticulationPoints(ni, d + 1)
                    childCount = childCount + 1
                    if low[ni] >= depth[i]
                        isArticulation = true
                    low[i] = Min(low[i], low[ni])
                else if ni <> parent[i]
                    low[i] = Min(low[i], depth[ni])
            if (parent[i] <> null and isArticulation) or (parent[i] == null and childCount > 1)
                Output i as articulation point
        */


        visited.add(i);
        depth.put(i, d);
        low.put(i, d);
        int childCount = 0;
        boolean isArticulation = false;

        Iterable<DagVertex<T>> iterable = Stream.concat(i.getSuccessors().stream(), i.getPredecessors().stream())::iterator;
        for (DagVertex<T> ni : iterable) {
            if (!visited.contains(ni)) {
                parent.put(ni, i);
                find(ni, d + 1);
                ++childCount;
                if (low.get(ni) > depth.get(i)) {
                    isArticulation = true;
                }
                low.put(i, Math.min(low.get(i), low.get(ni)));
            } else {
                if (ni != parent.get(i)) {
                    low.put(i, Math.min(low.get(i), depth.get(ni)));
                }
            }
        }
        if(parent.get(i) != null) {
            if(isArticulation) {
                out.add(i);
            }
        } else {
            if(includeRoot && childCount > 1) {
                out.add(i);
            }
        }
    }

    public static <T> Set<DagVertex<T>> find(DagVertex<T> n, boolean includeRoot) {
        ArticulationPoints<T> articulationPoints = new ArticulationPoints<>(includeRoot);
        articulationPoints.find(n, 0);
        return articulationPoints.out;
    }
}
